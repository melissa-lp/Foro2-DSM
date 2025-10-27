# 💰 Control de Gastos - Aplicación Android

Aplicación móvil para Android desarrollada con Kotlin y Jetpack Compose que permite a los usuarios gestionar sus gastos personales de manera eficiente, con autenticación segura mediante Firebase y almacenamiento en tiempo real con Cloud Firestore.
- Enlace a video de Youtube: https://youtu.be/YvRyf_1QO8g
## 📱 Características Principales

- ✅ **Autenticación Segura**
  - Inicio de sesión con correo electrónico y contraseña
  - Inicio de sesión con Google 
  - Gestión de sesiones persistentes

- ✅ **Gestión de Gastos (CRUD Completo)**
  - Agregar gastos con nombre, monto, categoría y descripción
  - Editar gastos existentes
  - Eliminar gastos con confirmación
  - Visualización en tiempo real

- ✅ **Filtros Avanzados**
  - Filtrar por categoría 
  - Filtrar por mes y año
  - Total mensual automático
  - Total filtrado dinámico

- ✅ **Validaciones Inteligentes**
  - Validación de formato de correo electrónico
  - Validación de montos (solo números, máximo 2 decimales)
  - Validación de campos obligatorios
  - Mensajes de error descriptivos en tiempo real

- ✅ **Diseño Moderno**
  - Interfaz Material Design 3
  - Paleta de colores personalizada
  - Responsive design
  - Animaciones fluidas

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Descripción |
|------------|---------|-------------|
| **Kotlin** | 1.9.0 | Lenguaje de programación principal |
| **Jetpack Compose** | 1.6.0 | Framework de UI declarativa |
| **Firebase Authentication** | 34.4.0 | Autenticación de usuarios |
| **Cloud Firestore** | 34.4.0 | Base de datos NoSQL en tiempo real |
| **Google Sign-In** | 21.2.0 | Autenticación con Google |
| **Material 3** | 1.2.0 | Sistema de diseño de Google |

## 🏗️ Arquitectura del Proyecto

```
app/src/main/java/com/udb/controlgastos/
├── data/                          # Capa de datos
│   ├── AuthRepository.kt         # Repositorio de autenticación
│   ├── ExpenseRepository.kt      # Repositorio de gastos
│   ├── Expense.kt                # Modelo de datos de gastos
│   ├── GoogleSignInHelper.kt     # Helper para Google Sign-In
│   └── Constants.kt              # Constantes del proyecto
│
├── viewmodel/                     # Capa de lógica de negocio
│   ├── AuthViewModel.kt          # ViewModel de autenticación
│   └── ExpenseViewModel.kt       # ViewModel de gastos
│
├── ui/                            # Capa de presentación
│   ├── screens/                  # Pantallas de la aplicación
│   │   ├── LoginScreen.kt       # Pantalla de inicio de sesión
│   │   ├── HomeScreen.kt        # Pantalla principal
│   │   ├── AddExpenseScreen.kt  # Agregar nuevo gasto
│   │   └── EditExpenseScreen.kt # Editar gasto existente
│   │
│   ├── components/               # Componentes reutilizables
│   │   └── FilterBar.kt         # Barra de filtros
│   │
│   └── theme/                    # Configuración de tema
│       ├── Color.kt             # Paleta de colores
│       ├── Theme.kt             # Tema principal
│       └── Type.kt              # Tipografía
│
└── MainActivity.kt               # Actividad principal y navegación
```


## 📊 Modelo de Datos

### Expense (Gasto)
```kotlin
data class Expense(
    val id: String,              // ID único del documento
    val userId: String,          // ID del usuario usuario
    val name: String,            // Nombre del gasto
    val amount: Double,          // Monto del gasto
    val category: String,        // Categoría del gasto
    val date: Timestamp,         // Fecha del gasto
    val description: String      // Descripción opcional
)
```

## 🚀 Instalación y Configuración

### Prerrequisitos

- Android Studio Hedgehog | 2023.1.1 o superior
- JDK 11 o superior
- Cuenta de Google/Firebase
- Dispositivo Android con API 24+ (Android 7.0) o emulador

### Paso 1: Clonar el Repositorio
```bash
git clone https://github.com/melissa-lp/Foro2-DSM.git
cd control-gastos
```

### Paso 2: Configurar Firebase

1. **Crear proyecto en Firebase Console:**
   - Ir a [Firebase Console](https://console.firebase.google.com/)
   - Crear un nuevo proyecto llamado `control-gastos`

2. **Habilitar Authentication:**
   - Ir a Authentication → Sign-in method
   - Habilitar **Email/Password**
   - Habilitar **Google** 

3. **Crear Firestore Database:**
   - Ir a Firestore Database → Create database
   - Seleccionar "Start in test mode"
   - Eligir ubicación: `us-central1`

4. **Registrar app Android:**
   - Ir a Project Settings
   - Click en "Add app" → Android
   - Package name: `com.udb.controlgastos`
   - Descargar `google-services.json`

### Paso 3: Configurar el Proyecto Local

1. **Colocar google-services.json en /app:**
```
   ControlGastos/
   └── app/
       ├── google-services.json
       └── src/
```

2. **Obtener SHA-1 (para Google Sign-In):**
   
   **Mac/Linux:**
```bash
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```
   
   **Windows:**
```bash
   keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android
```

3. **Agregar SHA-1 en Firebase:**
   - Copiar el SHA-1 obtenido
   - Ir a Firebase Console → Project Settings → Your apps
   - Agregar la huella digital SHA-1
   - Descargar nuevamente `google-services.json` actualizado

4. **Configurar Web Client ID:**
   - Ir a Firebase Console → Authentication → Sign-in method → Google
   - Copiar el **Web Client ID**
   - Abrir `app/src/main/java/com/udb/controlgastos/data/Constants.kt`
   - Reemplazar el valor:
```kotlin
   const val WEB_CLIENT_ID = "WEB_CLIENT_ID.apps.googleusercontent.com"
```

### Paso 4: Compilar y Ejecutar
```bash
# En Android Studio:
1. Build → Clean Project
2. Build → Rebuild Project
3. Run ▶️
```

## 👥 Configuración para Colaboradores

Para los colaboradores del proyecto, seguir estos pasos adicionales:

### 1. Obtener tu SHA-1
```bash
# Mac/Linux
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Windows
keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android
```

### 2. Enviar el SHA-1 al administrador del proyecto

El administrador debe:
- Ir a Firebase Console → Project Settings
- Agregar tu SHA-1 en "SHA certificate fingerprints"
- Descargar el nuevo `google-services.json`
- Hacer commit y push del archivo actualizado

### 3. Actualizar copia local
```bash
git pull origin main
```

### 4. Limpiar y reconstruir
```bash
Build → Clean Project
Build → Rebuild Project
Run ▶️
```
