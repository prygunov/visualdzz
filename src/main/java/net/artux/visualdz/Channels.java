package net.artux.visualdz;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.util.Arrays;
import java.util.Collections;

public class Channels {

    ChannelImage r;
    ChannelImage g;
    ChannelImage b;

    public Channels(ChannelImage r, ChannelImage g, ChannelImage b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    BufferedImage toImage(){
        BufferedImage image = new BufferedImage(r.getWidth(), r.getHeight(), BufferedImage.TYPE_INT_RGB);
        int[] targetPixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        int max = Collections.max(Arrays.asList(r.getBytesAsShort()));

        int c = (Integer.MAX_VALUE / max);
        System.out.println("C: " + c + " max: " + max);

        for(int i = 0; i < r.getBytesAsShort().length; i++) {
            targetPixels[3 * i] =  (r.getBytesAsShort()[i] * c);
            targetPixels[3 * i + 1] =  (g.getBytesAsShort()[i] * c);
            targetPixels[3 * i + 2] =  (b.getBytesAsShort()[i] * c);
        }

        //System.arraycopy(bytesAsInt, 0, targetPixels, 0, bytesAsInt.length);
        return image;
    }
}
