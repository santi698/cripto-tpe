# Secreto Compartido en Imágenes con Esteganografía

## Instalación y Dependencias

El programa ha sido desarrollado en Java utilizando Gradle como controlador de dependencias. Es por ésto que para una correcta ejecución es necesario instalar dicha build tool. Hacerlo de la siguiente manera:

Linux

```
sudo apt install gradle
```

Mac 

```
brew install gradle
```

## Ejecución

1. Posicionarse en la carpeta del proyecto y generar el jar. El mismo se guardará en la carpeta build/libs. Hacerlo con el siguiente comando:

```
gradle jar
```

2. Correr el jar que se haya en la carpeta build/libs agregando las configuraciones pedidas en el enunciado del trabajo práctico.

```
java -jar build/libs/TPE\ Implementación.jar #CONFIGURACIONES#
```

## Ejemplos de configuración


### Ejemplos de distribución de imágenes

```
java -jar build/libs/TPE\ Implementación.jar -k8 -n10 -secret src/main/resources/sin_secreto/James.bmp
 -dir src/main/resources/prueba -d
```

``` 
java -jar build/libs/TPE\ Implementación.jar -k8 -n16 -secret src/main/resources/sin_secreto/James.bmp
 -dir src/main/resources/prueba -d  
```
```
java -jar build/libs/TPE\ Implementación.jar -k8 -secret src/main/resources/sin_secreto/James.bmp
 -dir src/main/resources/prueba -d                        
``` 



### Ejemplo de recuperación de imágen

```
java -jar build/libs/TPE\ Implementación.jar -k8 -secret secret.bmp -dir src/main/resources/prueba -r
```
