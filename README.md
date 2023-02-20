# MyMaps
Aplicación de geolocalizador donde podemos ver nuestra ubicación en tiempo real o nos envía a un punto
por defecto en caso de no dar acceso al permiso de ubicación. También podemos agregar puntos como favoritos
(manteniendo presionado el punto a marcar como favorito) y darle un nombre para tenerlo como referencia, 
también podemos ver todos los puntos que tengamos marcados como favoritos. La lista de puntos favoritos se 
muestra en recycleview y con un botón para dirigirnos a uno de esos puntos.
Podemos acercar o alejar el zoom y desplazarnos por todo el mapa, también podemos volver a nuestro punto real,
al iniciar la app y alejar el zoom podemos observar 100 puntos de referencia que se consumen de un GeoJson.

La aplicación se realizó con el SDK de Mapbox, con lenguaje de programación Kotlin y se guardó la información
en SQLite.
