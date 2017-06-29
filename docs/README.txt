# Secreto Compartido en Imágenes con Esteganografía

## Descripción

Se ha realizado una implementación del algoritmo de Secreto Compartido en Imágenes descripto en los papers “An Efficient Secret Image Sharing Scheme” (Luang-Shyr Wu y Tsung-Ming) y “Secret image Sharing” (Thien y Lin), basados en el método de Secreto Compartido desarrollado por Adi Shamir y George Blakley.
El programa fue realizado en Java y cuenta con las siguientes funcionalidades:

1. Distribuye una imagen secreta de extensión “.bmp” en otras imágenes también de extensión “.bmp” que constituyen las sombras en un esquema (k, n) de secreto compartido. 

2. Recupera una imagen secreta de extensión “.bmp” a partir de k imágenes, también de extensión “.bmp”


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
