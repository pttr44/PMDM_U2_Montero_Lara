# EnrutamientoAvanzado

App de notas con Navigation Component, SafeArgs (Java) y deep links.
Unidad 2 · Módulo 0489 · CFGS DAM.

- Android Studio (JDK 21 embebido), AGP 8.7.3, Gradle 8.9, minSdk 24, compileSdk 35.
- Abrir la carpeta en Android Studio, esperar al Sync de Gradle y pulsar Run.
- Pantallas: Nueva nota -> Detalle de la nota -> Confirmación (ejercicio 3).
- Ejercicios de ampliación incluidos: 2 (valor por defecto) y 3 (tercera pantalla con popUpTo).

## Probar el deep link (con la app instalada y el emulador encendido)

PowerShell (Windows):

    & "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" shell am start -a android.intent.action.VIEW -d "notas://detalle/Comprar%20pan"

Linux/macOS:

    adb shell am start -a android.intent.action.VIEW -d "notas://detalle/Comprar%20pan"
