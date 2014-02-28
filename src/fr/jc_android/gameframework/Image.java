package fr.jc_android.gameframework;

import fr.jc_android.gameframework.Graphics.ImageFormat;

public interface Image {
    public int getWidth();
    public int getHeight();
    public ImageFormat getFormat();
    public void dispose();
}