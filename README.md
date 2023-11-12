# Trabajando con Streams en Java

Usando `Stream` en Java, podemos simplificar varias operaciones como el filtrado, el mapeo, conversiones y más. Sin embargo, a veces no es del todo claro distinguir cuándo una operación nos devuelve otro `Stream` para seguir trabajando y cuándo nos proporciona un resultado final.

## Clarificando el Uso de Streams

¡Pero esto no tiene por qué ser confuso!

### Delegación a través de Lambdas

Cuando hablamos de pasar lambdas a una operación de `Stream`, en realidad, estamos delegando a Java la tarea de crear un objeto basado en una interfaz.

#### Ejemplo de Uso de Streams con Lambdas

```java
Stream<String> coursesStream = Utils.getListOf("Java", "Node.js", "Kotlin").stream();

Stream<String> javaCoursesStream = coursesStream.filter(course -> course.contains("Java"));

// En realidad, es lo mismo que:

Stream<String> explicitOperationStream = coursesStream.filter(new Predicate<String>() {
    public boolean test(String st) {
        return st.contains("Java");
    }
});
```

# Repaso de Interfaces

Estas interfaces las mencionamos en clases anteriores. Solo como repaso, listo algunas a continuación:

- `Consumer<T>`: recibe un dato de tipo `T` y no genera ningún resultado.
- `Function<T,R>`: toma un dato de tipo `T` y genera un resultado de tipo `R`.
- `Predicate<T>`: toma un dato de tipo `T` y evalúa si el dato cumple una condición.
- `Supplier<T>`: no recibe ningún dato, pero genera un dato de tipo `T` cada vez que es invocado.
- `UnaryOperator<T>`: recibe un dato de tipo `T` y genera un resultado de tipo `T`.

Estas interfaces (y otras más) sirven como la base de donde generar los objetos con las lambdas que pasamos a los diferentes métodos de `Stream`. Cada una de ellas cumple esencialmente con recibir el tipo de dato del `Stream` y generar el tipo de retorno que el método espera.

## Ejemplo de Implementación Propia de Stream

Si tuvieras tu propia implementación de `Stream`, se vería similar al siguiente ejemplo:



```java
public class PlatziStream<T> implements Stream {
    private List<T> data;

    public Stream<T> filter(Predicate<T> predicate) {
        List<T> filteredData = new LinkedList<>();
        for(T t : data){
            if(predicate.test(t)){
                filteredData.add(t);
            }
        }

        return filteredData.stream();
    }
}
```

## Uso del Predicate en Stream

Probablemente, tendría otros métodos y estructuras de datos, pero la parte que importa es justamente cómo se usa el `Predicate`. Lo que hace `Stream` internamente es pasar cada dato por este objeto que nosotros proveemos como una lambda y, según el resultado de la operación, decidir si debe incluirse o no en el `Stream` resultante.

Como puedes notar, esto no tiene mucha complejidad, puesto que es algo que pudimos fácilmente replicar. Pero `Stream` no solo incluye estas operaciones “triviales”, también incluye un montón de utilidades para que la máquina virtual de Java pueda operar los elementos de un `Stream` de manera más rápida y distribuida.

### Operaciones

A estas funciones que reciben lambdas y se encargan de trabajar (operar) sobre los datos de un `Stream` generalmente se les conoce como Operaciones.

Existen dos tipos de operaciones: intermedias y finales.

Cada operación aplicada a un `Stream` hace que el `Stream` original ya no sea usable para más operaciones. Es importante recordar esto, pues tratar de agregar operaciones a un `Stream` que ya está siendo procesado es un error muy común.

En este punto seguramente te parezcan familiares todas estas operaciones, pues vienen en forma de métodos de la interfaz `Stream`. Y es cierto. Aunque son métodos, se les considera operaciones, puesto que su intención es operar el `Stream` y, posterior a su trabajo, el `Stream` no puede volver a ser operado.

En clases posteriores hablaremos más a detalle sobre cómo identificar una operación terminal de una operación intermedia.

### Collectors

Una vez que has agregado operaciones a tu `Stream` de datos, lo más usual es que llegues a un punto donde ya no puedas trabajar con un `Stream` y necesites enviar tus datos en otro formato, por ejemplo, JSON o una List a base de datos.

Existe una interfaz única que combina todas las interfaces antes mencionadas y que tiene como única utilidad proveer de una operación para obtener todos los elementos de un `Stream`: `Collector`.

`Collector<T, A, R>` es una interfaz que tomará datos de tipo `T` del `Stream`, un tipo de dato mutable `A`, donde se irán agregando los elementos (mutable implica que podemos cambiar su contenido, como un `LinkedList`), y generará un resultado de tipo `R`.

Suena complicado… y lo es. Por eso mismo, Java 8 incluye una serie de `Collectors` ya definidos para no rompernos las cabeza con cómo convertir nuestros datos.

#### Ejemplo de Collectors

```java
public List<String> getJavaCourses(Stream<String> coursesStream) {
    List<String> javaCourses =
        coursesStream.filter(course -> course.contains("Java"))
            .collect(Collectors.toList());

    return javaCourses;
}
```

## Uso de `java.util.stream.Collectors`

Usando `java.util.stream.Collectors` podemos convertir muy sencillamente un `Stream` en un `Set`, `Map`, `List`, `Collection`, etc. La clase `Collectors` ya cuenta con métodos para generar un `Collector` que corresponda con el tipo de dato que tu `Stream` está usando. Incluso vale la pena resaltar que `Collectors` puede generar un `ConcurrentMap` que puede ser de utilidad si requieres de múltiples threads.

Usar `Collectors.toXXX` es el proceso inverso de usar `Collection.stream()`. Esto hace que sea fácil generar APIs públicas que trabajen con estructuras/colecciones comunes e internamente utilizar `Stream` para agilizar las operaciones de nuestro lado.

### Tipos de Retorno

Hasta este punto, la única manera de obtener un dato que ya no sea un `Stream` es usando `Collectors`, pues la mayoría de operaciones de `Stream` se enfocan en operar los datos del `Stream` y generar un nuevo `Stream` con los resultados de la operación.

Sin embargo, algunas operaciones no cuentan con un retorno. Por ejemplo, `forEach`, que es una operación que no genera ningún dato. Para poder entender qué hace cada operación basta con plantear qué hace la operación para poder entender

Las operaciones terminales son aquellas operaciones que como resultado no generan un nuevo `Stream`. Su resultado puede variar según la operación. La utilidad de estas es poder generar un valor final a todas nuestras operaciones o consumir los datos finales. La razón principal para querer esto es que los datos deberán salir en algún punto de nuestro control y es con las operaciones terminales que hacemos esto.

Pensemos, por ejemplo, en un servidor web. Recibe una petición de datos, convierte la petición en un `Stream<JSON>`, procesa los datos usando `filter` o `map`, convierte de JSON a datos locales que sean manipulables por código Java y hace consumo de una base de datos. Todo esto mediante streams de diferentes tipos. Pero eventualmente tiene que devolver una respuesta para quien le hizo la petición.

¿Qué pasa si quien hizo la petición no esta usando Java? No podemos enviarle un objeto de tipo `Stream` a un código hecho en Python o en JavaScript… es ahi donde una operación final nos ayuda a convertir nuestro `Stream` de Java en algún tipo de dato que sea mas comprensible.

Otro ejemplo claro es si estamos creando una librería o creando código que más gente en nuestro equipo usará. Al crear nuestros métodos y clases usamos streams por aquí y lambdas por allá, pero al exponer estos métodos para uso de otros desarrolladores no podemos obligarlos a usar `Stream`.

Las razones son variadas. No queremos obligar y limitar a quienes usen nuestro código a trabajar con un solo tipo dato. No sabemos qué versión de Java está usando quien use nuestro código. No sabemos si `Stream` está disponible en su parte del código (por ejemplo, en Android no estaba disponible del todo), etc.

Es por ello que quisiéramos proveer de algo mas simple: listas, primitivos o incluso dar algún mecanismo para poder usar código externo de nuestro lado.

Las operaciones terminales más comunes que se encuentran en `Stream` son:
- `anyMatch()`
- `allMatch()`
- `noneMatch()`
- `findAny()`
- `findFirst()`
- `min()`
- `max()`
- `reduce()`
- `count()`
- `toArray()`
- `collect()`
- `forEach()`

Revisaremos qué hacen y qué utilidad tienen durante esta lectura.

## Operaciones terminales de coincidencia
### anyMatch, allMatch, noneMatch
Las operaciones `anyMatch`, `allMatch` y `noneMatch` sirven para determinar si en un `Stream` hay elementos que cumplan con un cierto `Predicate`. Esto puede ser una forma simple de validar los datos de un `Stream`. Son terminales pues las tres retornan un `boolean`:

```java
//Nos indica si un stream contiene un elemento según el Predicate que le pasemos:
Stream<Integer> numbersStream = Stream.of(1, 2, 3, 4, 5, 6, 7, 11);
boolean biggerThanTen = numbersStream.anyMatch(i -> i > 10); //true porque tenemos el 11

//allMatch
//Nos indica si todos los elementos de un Stream cumplen con un cierto Predicate:
Stream<Integer> agesStream = Stream.of(19, 21, 35, 45, 12);
boolean allLegalDrinkingAge = agesStream.allMatch(age -> age > 18); //false, tenemos un menor

//noneMatch
//Nos indica si todos los elementos de un Stream NO CUMPLEN un cierto Predicate:
Stream<Integer> oddNumbers = Stream.of(1, 3, 5, 7, 9, 11);
boolean allAreOdd = oddNumbers.noneMatch(i -> i % 2 == 0);
```


## Operaciones terminales de búsqueda
### findAny, findFirst
Estas operaciones retornan un `Optional<T>` como resultado de buscar un elemento dentro del `Stream`.

La diferencia entre ambas es que `findFirst` retornara un `Optional` conteniendo el primer elemento en el `Stream` si el `Stream` tiene definida previamente una operación de ordenamiento o para encontrar elementos. De lo contrario, funcionará igual que `findAny`, tratando de devolver cualquier elemento presente en el `Stream` de forma no determinista (random)

Si el elemento encontrado es `null`, tendrás que lidiar con una molesta `NullPointerException`. Si el `Stream` esta vacío, el retorno es equivalente a `Optional.empty()`.

La principal razón para usar estas operaciones es poder usar los elementos de un `Stream` después haber filtrado y convertido tipos de datos. Con `Optional` nos aseguramos que, aún si no hubiera resultados, podremos seguir trabajando sin excepciones o escribiendo condicionales para validar los datos.

## Operaciones terminales de reducción
### min, max
Son dos operaciones cuya finalidad es obtener el elemento más pequeño (`min`) o el elemento más grande (`max`) de un `Stream` usando un `Comparator`. Puede haber casos de `Stream` vacíos, es por ello que las dos operaciones retornan un `Optional` para en esos casos poder usar `Optional.empty`.

La interfaz `Comparator` es una `@FunctionalInterface`, por lo que es sencillo usar `min` y `max` con lambdas:

```java
Stream<Long> bigNumbers = Stream.of(100L, 200L, 1000L, 5L);
Optional<Long> minimumOptional = bigNumbers.min((numberX, numberY) -> (int) Math.min(numberX, numberY));
```

### reduce
Esta operación existe en tres formas:
- `reduce(valorInicial, BinaryOperator)`
- `reduce(BinaryAccumulator)`
- `reduce(valorInicial, BinaryFunction, BinaryOperator)`

La diferencia entre los 3 tipos de invocación:

#### reduce(BinaryAccumulator)
Retorna un Optional del mismo tipo que el Stream, con un solo valor resultante de aplicar el `BinaryAccumulator` sobre cada elemento o `Optional.empty()` si el stream estaba vacío. Puede generar un `NullPointerException` en casos donde el resultado de `BinaryAccumulator` sea `null`.

```java
Stream<String> aLongStoryStream = Stream.of("Cuando", "despertó,", "el", "dinosaurio", "todavía", "estaba", "allí.");
Optional<String> longStoryOptional = aLongStoryStream.reduce((previousStory, nextPart) -> previousStory + " " + nextPart);
longStoryOptional.ifPresent(System.out::println); //"Cuando despertó, el dinosaurio todavía estaba allí."
```


Y el caso mas interesante…

#### reduce(valorInicial, BinaryFunction<V, T, V>, BinaryOperator<V>)
Genera un valor de tipo `V` después de aplicar `BinaryFunction` sobre cada elemento de tipo `T` en el `Stream` y obtener un resultado `V`.

Esta version de reduce usa el `BinaryFunction` como `map + reduce`. Es decir, por cada elemento en el `Stream` se genera un valor `V` basado en el `valorInicial` y el resultado anterior de la `BinaryFunction`. `BinaryOperator` se utiliza en streams paralelos (`stream.parallel()`) para determinar el valor que se debe mantener en cada iteración.

```java
Stream<String> aLongStoryStreamAgain = Stream.of("Cuando", "despertó,", "el", "dinosaurio", "todavía", "estaba", "allí.");
int charCount = aLongStoryStreamAgain.reduce(0, (count, word) -> count + word.length(), Integer::sum);
```


## count
Una operación sencilla: sirve para obtener cuantos elementos hay en el `Stream`.

```java
Stream<Integer> yearsStream = Stream.of(1990, 1991, 1994, 2000, 2010, 2019, 2020);
long yearsCount = yearsStream.count(); //7, solo nos dice cuantos datos tuvo el stream.
```

La principal razón de usar esta operación es que, al aplicar `filter` o `flatMap`, nuestro `Stream` puede crecer o disminuir de tamaño y, tal vez, de muchas operaciones solo nos interese saber cuántos elementos quedaron presentes en el `Stream`. Por ejemplo, cuantos archivos se borraron o cuantos se crearon por ejemplo.

## toArray
Agrega todos los elementos del `Stream` a un arreglo y nos retorna dicho arreglo. La operación genera un `Object[]`, pero es sposible hacer castings al tipo de dato del `Stream`.

## collect
Mencionamos la operación `collect` en la lectura sobre _operaciones y collectors_, donde mencionamos que:

> `Collector<T, A, R>` es una interfaz que tomara datos de tipo `T` del `Stream`, un tipo de dato mutable `A`, donde se irán agregando los elementos (mutable implica que podemos cambiar su contenido, como un `LinkedList`) y generara un resultado de tipo `R`.
>
> Usando `java.util.stream.Collectors` podemos convertir sencillamente un `Stream` en un `Set`, `Map`, `List`, `Collection`, etc. La clase `Collectors` ya cuenta con métodos para generar un `Collector` que corresponda con el tipo de dato que tu `Stream` esta usando. Incluso vale la pena resaltar que `Collectors` puede generar un `ConcurrentMap` que puede ser de utilidad si requieres de multiples threads.

```java
public List<String> getJavaCourses(Stream<String> coursesStream) {
    List<String> javaCourses =
        coursesStream.filter(course -> course.contains("Java"))
            .collect(Collectors.toList());

    return javaCourses;
}
```

## Operaciones terminales de iteración
### forEach
Tan simple y tan lindo como un clásico `for`. `forEach` es una operación que recibe un `Consumer<T>` y no tiene un valor de retorno (`void`). La principal utilidad de esta operación es dar un uso final a los elementos del `Stream`.

```java
Stream<List<String>> courses = getCourses();
courses.forEach(courseList -> System.out.println("Cursos disponibles: " + courseList));
```
