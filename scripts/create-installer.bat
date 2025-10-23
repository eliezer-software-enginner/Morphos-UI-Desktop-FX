@echo off
setlocal enableDelayedExpansion

REM --- Application Configuration ---
set "APP_NAME=morphos_desktop_fx"
set "APP_VERSION=v1.0"
set "APP_VENDOR=Eliezer known as Plantfall"
set "APP_COPYRIGHT=Copyright 2025"
set "APP_DESCRIPTION=Morphos Desktop Fx is a UI builder for making UI rapidly and easily, and also generate pure java code for you copy and paste in your application"
set "APP_MAIN_CLASS=my_app.App"
set "APP_ICON=src\main\resources\assets\app_ico.ico"
set "JAR_FILE=my_app-%APP_VERSION%-jar-with-dependencies.jar"
set "FX_MODULES=javafx.controls,javafx.fxml,javafx.graphics,java.sql,javafx.media,javafx.web"

echo Checking requirements to create the installer...

REM --- 1. Requirements Check ---
if "%JAVA_HOME%"=="" (
    echo ERROR: JAVA_HOME not set! Please configure your JDK path.
    pause
    exit /b 1
)

where jpackage >nul 2>nul
if !errorlevel! neq 0 (
    echo ERROR: jpackage not found! Ensure JDK is installed and in PATH.
    pause
    exit /b 1
)

REM Check JDK version (Using a more standard version check for robustness)
for /f "tokens=3" %%a in ('java -version 2^>^&1 ^| findstr /i "version"') do set "java_version=%%a"
set "major_version=!java_version:~1,2!"
if "!major_version!"=="" (
    echo WARNING: Could not reliably check JDK version. Proceeding...
) else if !major_version! lss 21 (
    echo WARNING: JDK version is less than 21. Current: !major_version!
)

REM WiX Toolset Check
set "WIX_PATH="
for %%d in (
    "C:\Program Files (x86)\WiX Toolset v3.14\bin"
    "C:\Program Files\WiX Toolset v3.14\bin"
    "C:\Program Files (x86)\WiX Toolset v3.11\bin"
    "C:\Program Files\WiX Toolset v3.11\bin"
) do (
    if exist "%%~d\light.exe" (
        set "WIX_PATH=%%~d"
        goto :wix_found
    )
)
echo ERROR: WiX Toolset not found! Install it to create the MSI.
pause
exit /b 1

:wix_found
echo WiX Toolset found at: %WIX_PATH%
set "PATH=%WIX_PATH%;%PATH%"
echo All requirements met!
echo.

REM --- 2. Cleanup and Preparation ---
echo Cleaning temporary and output directories...
REM **CLEANUP CONSOLIDADO:** Remove tudo que está em 'build' e '..\dist'
if exist "build" rmdir /s /q "build"
if exist "dist" rmdir /s /q "dist"

REM Cria as pastas root
mkdir build
mkdir dist

REM Cria pasta temporária para arquivos de entrada do jpackage
mkdir build\input_app

REM Copia JAR e dependências JavaFX para o diretório de entrada
copy "target\%JAR_FILE%" build\input_app\
xcopy /E /I /Y ".java_fx\lib" "build\input_app\lib"

REM --- 3. JLink: Create Runtime Image (JRE) ---
echo Creating custom runtime image (JRE) with JLink...
jlink ^
    --module-path ".java_fx/lib" ^
    --add-modules %FX_MODULES% ^
    --output build\runtime

if !errorlevel! neq 0 (
    echo ERROR: JLink failed to create the runtime image.
    pause
    exit /b 1
)

REM **REMOVIDO: Cópia de DLLs para build\runtime\bin é desnecessária aqui. O jlink não usa esses arquivos.**
REM **A cópia só é necessária para o diretório final da aplicação (Passo 5).**


REM --- 4. JPackage: Create Application Image (.app) ---
echo Creating Windows Application Image...
jpackage ^
    --input build\input_app ^
    --dest dist ^
    --main-jar "%JAR_FILE%" ^
    --main-class %APP_MAIN_CLASS% ^
    --name %APP_NAME% ^
    --app-version %APP_VERSION% ^
    --vendor "%APP_VENDOR%" ^
    --copyright "%APP_COPYRIGHT%" ^
    --description "%APP_DESCRIPTION%" ^
    --type app-image ^
    --runtime-image build\runtime ^
    --java-options "-Djava.library.path=bin" ^
    --java-options "-Dprism.order=sw" ^
    --java-options "--enable-native-access=javafx.graphics" ^
    --icon "%APP_ICON%"

if !errorlevel! neq 0 (
    echo ERROR: JPackage failed to create the application image.
    pause
    exit /b 1
)

REM --- 5. Fix DLLs (CRUCIAL FIX: Copia DLLs para a imagem final) ---
echo Copying necessary JavaFX DLLs to final application image...
xcopy /E /I /Y ".java_fx\bin\*.dll" "dist\%APP_NAME%\bin\"

REM --- 6. JPackage: Create MSI Installer (CORRIGIDO) ---
echo Application image created successfully!
echo Creating MSI installer...

jpackage ^
    --app-image dist\%APP_NAME% ^
    --name %APP_NAME% ^
    --app-version %APP_VERSION% ^
    --vendor "%APP_VENDOR%" ^
    --copyright "%APP_COPYRIGHT%" ^
    --description "%APP_DESCRIPTION%" ^
    --type msi ^
    --dest dist ^
    --win-menu ^
    --win-menu-group "%APP_NAME%" ^
    --win-shortcut ^
    --win-dir-chooser ^
    --win-per-user-install ^
    --icon "%APP_ICON%"

if !errorlevel! neq 0 (
    echo ERROR: JPackage failed to create the MSI installer.
    pause
    exit /b 1
)

REM --- 7. Finalize (SIMPLIFICADO) ---
REM O comando 'move' foi removido pois o --dest do jpackage ja colocou o MSI em ..\dist
echo.
echo Installer created successfully!
echo The installer file is at: dist\%APP_NAME%-%APP_VERSION%.msi
echo.

REM Clean ALL temporary build directories
rmdir /s /q build
endlocal
pause