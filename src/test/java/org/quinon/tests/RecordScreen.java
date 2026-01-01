package org.quinon.tests;

import org.monte.media.Format;
import org.monte.media.Registry;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class RecordScreen extends ScreenRecorder {

    // Constructor que pasa los parámetros necesarios al constructor de la clase base ScreenRecorder
    public RecordScreen(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat, Format screenFormat,
                        Format mouseFormat, Format audioFormat, File movieFolder)
            throws IOException, AWTException {
        super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
    }

    @Override
    protected File createMovieFile(Format fileFormat) throws IOException {
        // Verificar si el directorio para guardar el video existe, si no, se crea
        if (!movieFolder.exists()) {
            movieFolder.mkdirs();  // Crear los directorios necesarios si no existen
        } else if (!movieFolder.isDirectory()) {
            // Si movieFolder no es un directorio, lanzar una excepción
            throw new IOException("\"" + movieFolder + "\" no es un directorio.");
        }
        // Crear un archivo de video con un nombre único basado en la hora actual
        return new File(movieFolder, "VideoRecording_" + System.currentTimeMillis() + "." +
                Registry.getInstance().getExtension(fileFormat));
    }
}
