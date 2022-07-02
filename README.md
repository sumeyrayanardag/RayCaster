# RayCaster
Ray Caster with JAVA

In this project, there are json files and this files hold informations about objects like radius, camera position, transformation. RayCaster read json files and creates images based on the information in the files.

### Scene1

{
	"orthocamera" : {
		"center" : [0, 0, 10],
		"direction" : [0, 0, -1],
		"up" : [0, 1, 0],
		"size" : 5
	},
	"background" : {
		"color" : [0, 0, 0]
	},
	"group" : [
		{ 
			"sphere" : {
				"center" : [0, 0, 0],
				"radius" : 1,
				"color" : [200, 200, 50]
			}
		}
	]
} 

![Scene1](https://user-images.githubusercontent.com/45365584/177009549-feade3a0-c72d-486a-9010-fe28cb73c78b.jpg)

