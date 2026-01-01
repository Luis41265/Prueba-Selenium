package org.quinon.tests;


// Importación de bibliotecas necesarias

import io.github.bonigarcia.wdm.WebDriverManager;
import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.awt.Rectangle;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class DocSeleniumTest {
    // variables a utilizar
    private WebDriver driver;          // inicializacion y cierre del webDriver
    private WebDriverWait wait;        // Espera explícita para elementos
    private ScreenRecorder screenRecorder;  // Grabacion de la pantalla durante el test


    /**

     * Configurar e iniciar la grabación de pantalla.

     * La grabación cubre toda la pantalla y se guarda en el directorio "videos".

     */
    @BeforeClass
    public void initializeTestEnvironment() {

        try {

            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration();
            File movieDir = new File("videos");

            Format fileFormat = new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI);
            Format screenFormat = new Format(MediaTypeKey, MediaType.VIDEO,
                    EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                    CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                    DepthKey, 24,
                    FrameRateKey, new Rational(15, 1),
                    QualityKey, 1.0f,
                    KeyFrameIntervalKey, 15 * 60);
            Format mouseFormat = new Format(MediaTypeKey, MediaType.VIDEO,
                    EncodingKey, "black",
                    FrameRateKey, new Rational(30, 1));
            Format audioFormat = null;  // Sin audio en este caso

            // se inicializa la grabacion
            screenRecorder = new RecordScreen(gc,
                    new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()),
                    fileFormat, screenFormat, mouseFormat, audioFormat, movieDir);
            screenRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // se gestiona configuracion  de Selenium con WebDriverManager para el driver del navegador
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));  // Configuración de la espera
        driver.manage().window().maximize();  // se  Maximiza la ventana del navegador
    }

    @Test
    public void openTestSeleniumDocumentation() {
        // Test para buscar la documentación de Selenium en Google
        driver.get("https://www.google.com");  // Navegar a Google
        waitUntilPageIsLoaded();  // Esperar a que la página se cargue completamente
        saveScreenshot("01_google_home");  // Tomar captura de pantalla

        // busqueda "Documentación de Selenium" en Google


        driver.get("https://www.selenium.dev/documentation/");
        waitUntilPageIsLoaded();
        saveScreenshot("03_selenium_documentation");  // Toma captura de pantalla de la documentación

        // navegacion del menu lateral
        String[] menuItems = {"Overview", "WebDriver", "Selenium Manager", "Grid", "IE Driver Server", "IDE", "Test Practices", "Legacy", "About"};
        for (String item : menuItems) {
            try {
                WebElement menuItem = wait.until(ExpectedConditions.elementToBeClickable(By.linkText(item)));
                menuItem.click();
                waitUntilPageIsLoaded();
                saveScreenshot("success_" + item.replace(" ", "_"));
            } catch (Exception e) {
                // Si no se encuentra el elemento, imprimir un error y tomar captura de pantalla
                System.err.println("No se encontró el elemento del menú: " + item);
                saveScreenshot("error_" + item.replace(" ", "_"));
            }
        }
    }

    private void saveScreenshot(String filename) {
        // tomar capturas de pantalla y guardarlas
        if (driver != null) {
            try {
                // Tomar captura de pantalla y guardar en un archivo
                File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Files.createDirectories(Paths.get("capturasDePantallas"));  // Crear directorio si no existe
                Files.copy(srcFile.toPath(), Paths.get("capturasDePantallas/" + filename + ".png"), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Captura guardada: " + filename);  // Imprimir mensaje de éxito
            } catch (WebDriverException e) {
                System.err.println(" No se pudo tomar la captura: " + filename);  // Si no se pudo tomar la captura
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método para esperar a que la página termine de cargar
     * Se valida el estado 'complete' del DOM.
     */
    private void waitUntilPageIsLoaded() {

        try {
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState")
                    .equals("complete"));
        } catch (TimeoutException e) {
            System.err.println(" La página tardó demasiado en cargar.");
        }
    }

    @AfterClass
    public void releaseResources() {
        // Configuración después de la ejecución de los tests
        if (driver != null) {
            driver.quit();  // Cerrar el navegador
        }
        // Finalizar la grabación de la pantalla
        try {
            if (screenRecorder != null) {
                screenRecorder.stop();  // Detener la grabación
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
