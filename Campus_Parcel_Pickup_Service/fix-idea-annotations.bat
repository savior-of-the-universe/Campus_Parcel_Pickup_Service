@echo off
echo ========================================
echo IDEA Lombok Annotation Processor Fix
echo ========================================
echo.

echo This script will help you configure IDEA for Lombok annotation processing.
echo.

echo Step 1: Open IDEA Settings
echo ---------------------------
echo Please open IDEA and go to:
echo File -^> Settings (or Ctrl+Alt+S)
echo.

echo Step 2: Navigate to Annotation Processors
echo -----------------------------------------
echo In the Settings dialog, navigate to:
echo Build, Execution, Deployment -^> Compiler -^> Annotation Processors
echo.

echo Step 3: Enable Annotation Processing
echo -------------------------------------
echo Please ensure the following settings are configured:
echo 1. Check the box: "Enable annotation processing"
echo 2. Select radio button: "Obtain processors from project classpath"
echo 3. Store generated sources relative to: "Module content root"
echo.

echo Step 4: Additional Settings
echo ---------------------------
echo Also check these settings:
echo Build, Execution, Deployment -^> Compiler -^> Java Compiler
echo - Ensure "Use '--release' option" is UNCHECKED
echo - Set "Additional command line parameters" to: -parameters
echo.

echo Step 5: Refresh and Rebuild
echo ---------------------------
echo After making these changes:
echo 1. Click "Apply" and "OK"
echo 2. Go to File -^> Invalidate Caches and Restart -^> Invalidate and Restart
echo 3. After restart, go to Build -^> Rebuild Project
echo.

echo Alternative: Manual Registry Edit
echo ----------------------------------
echo If the above doesn't work, you can try editing IDEA's registry:
echo 1. Go to Help -^> Find Action (Ctrl+Shift+A)
echo 2. Type "Registry" and press Enter
echo 3. Find and enable: "compiler.automake.allow.when.app.running"
echo 4. Find and enable: "ide.balloon.shadow.enabled"
echo.

echo ========================================
echo Configuration Complete!
echo ========================================
echo.
echo After following these steps, your Lombok annotations should work properly.
echo If issues persist, try running: mvn clean compile -X
echo.
pause
