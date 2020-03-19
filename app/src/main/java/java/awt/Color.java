package java.awt;

import java.io.Serializable;

public class Color implements Serializable {
    public static final Color black = new Color(0, 0, 0);
    public static final Color blue = new Color(0, 0, 255);
    public static final Color cyan = new Color(0, 255, 255);
    public static final Color darkGray = new Color(64, 64, 64);
    public static final Color gray = new Color(128, 128, 128);
    public static final Color green = new Color(0, 255, 0);
    public static final Color lightGray = new Color(192, 192, 192);
    public static final Color magenta = new Color(255, 0, 255);
    public static final Color orange = new Color(255, 200, 0);
    public static final Color pink = new Color(255, 175, 175);
    public static final Color red = new Color(255, 0, 0);
    private static final long serialVersionUID = 118526816881161077L;
    public static final Color white = new Color(255, 255, 255);
    public static final Color yellow = new Color(255, 255, 0);
	public static final Color BLACK = black;
    public static final Color BLUE = blue;
    public static final Color CYAN = cyan;
    public static final Color DARK_GRAY = darkGray;
    public static final Color GRAY = gray;
    public static final Color GREEN = green;
    public static final Color LIGHT_GRAY = lightGray;
    public static final Color MAGENTA = magenta;
    private static final int MIN_SCALABLE = 3;
    public static final Color ORANGE = orange;
    public static final Color PINK = pink;
    public static final Color RED = red;
    private static final double SCALE_FACTOR = 0.7d;
    public static final Color WHITE = white;
    public static final Color YELLOW = yellow;
    private float falpha;
    private float[] frgbvalue;
    private float[] fvalue;
    int value;

    public Color(int rgba, boolean hasAlpha) {
        if (hasAlpha) {
            this.value = rgba;
        } else {
            this.value = -16777216 | rgba;
        }
    }

    public Color(int r, int g, int b, int a) {
        if ((r & 255) == r && (g & 255) == g && (b & 255) == b && (a & 255) == a) {
            this.value = (((g << 8) | b) | (r << 16)) | (a << 24);
            return;
        }
        throw new IllegalArgumentException("Color parameter outside of expected range");
    }

    public Color(int r, int g, int b) {
        if ((r & 255) == r && (g & 255) == g && (b & 255) == b) {
            this.value = (((g << 8) | b) | (r << 16)) | -16777216;
            return;
        }
        throw new IllegalArgumentException("Color parameter outside of expected range");
    }

    public Color(int rgb) {
        this.value = -16777216 | rgb;
    }

    public Color(float r, float g, float b, float a) {
        this((int) (((double) (r * 255.0f)) + 0.5d), (int) (((double) (g * 255.0f)) + 0.5d), (int) (((double) (b * 255.0f)) + 0.5d), (int) (((double) (a * 255.0f)) + 0.5d));
        this.falpha = a;
        this.fvalue = new float[MIN_SCALABLE];
        this.fvalue[0] = r;
        this.fvalue[1] = g;
        this.fvalue[2] = b;
        this.frgbvalue = this.fvalue;
    }

    public Color(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    public String toString() {
        return getClass().getName() + "[r=" + getRed() + ",g=" + getGreen() + ",b=" + getBlue() + "]";
    }

    public boolean equals(Object obj) {
        if ((obj instanceof Color) && ((Color) obj).value == this.value) {
            return true;
        }
        return false;
    }

    public Color darker() {
        return new Color((int) (((double) getRed()) * SCALE_FACTOR), (int) (((double) getGreen()) * SCALE_FACTOR), (int) (((double) getBlue()) * SCALE_FACTOR));
    }

    public Color brighter() {
        int r = getRed();
        int b = getBlue();
        int g = getGreen();
        if (r == 0 && b == 0 && g == 0) {
            return new Color((int) MIN_SCALABLE, (int) MIN_SCALABLE, (int) MIN_SCALABLE);
        }
        if (r >= MIN_SCALABLE || r == 0) {
            r = (int) (((double) r) / SCALE_FACTOR);
            if (r > 255) {
                r = 255;
            }
        } else {
            r = MIN_SCALABLE;
        }
        if (b >= MIN_SCALABLE || b == 0) {
            b = (int) (((double) b) / SCALE_FACTOR);
            if (b > 255) {
                b = 255;
            }
        } else {
            b = MIN_SCALABLE;
        }
        if (g >= MIN_SCALABLE || g == 0) {
            g = (int) (((double) g) / SCALE_FACTOR);
            if (g > 255) {
                g = 255;
            }
        } else {
            g = MIN_SCALABLE;
        }
        return new Color(r, g, b);
    }

    public float[] getRGBComponents(float[] components) {
        if (components == null) {
            components = new float[4];
        }
        if (this.frgbvalue != null) {
            components[MIN_SCALABLE] = this.falpha;
        } else {
            components[MIN_SCALABLE] = ((float) getAlpha()) / 255.0f;
        }
        getRGBColorComponents(components);
        return components;
    }

    public float[] getRGBColorComponents(float[] components) {
        if (components == null) {
            components = new float[MIN_SCALABLE];
        }
        if (this.frgbvalue != null) {
            components[2] = this.frgbvalue[2];
            components[1] = this.frgbvalue[1];
            components[0] = this.frgbvalue[0];
        } else {
            components[2] = ((float) getBlue()) / 255.0f;
            components[1] = ((float) getGreen()) / 255.0f;
            components[0] = ((float) getRed()) / 255.0f;
        }
        return components;
    }

    public float[] getComponents(float[] components) {
        if (this.fvalue == null) {
            return getRGBComponents(components);
        }
        int nColorComps = this.fvalue.length;
        if (components == null) {
            components = new float[(nColorComps + 1)];
        }
        getColorComponents(components);
        components[nColorComps] = this.falpha;
        return components;
    }

    public float[] getColorComponents(float[] components) {
        if (this.fvalue == null) {
            return getRGBColorComponents(components);
        }
        if (components == null) {
            components = new float[this.fvalue.length];
        }
        for (int i = 0; i < this.fvalue.length; i++) {
            components[i] = this.fvalue[i];
        }
        return components;
    }

    public int hashCode() {
        return this.value;
    }

    public int getRed() {
        return (this.value >> 16) & 255;
    }

    public int getRGB() {
        return this.value;
    }

    public int getGreen() {
        return (this.value >> 8) & 255;
    }

    public int getBlue() {
        return this.value & 255;
    }

    public int getAlpha() {
        return (this.value >> 24) & 255;
    }

    public static Color getColor(String nm, Color def) {
        Integer integer = Integer.getInteger(nm);
        return integer == null ? def : new Color(integer.intValue());
    }

    public static Color getColor(String nm, int def) {
        Integer integer = Integer.getInteger(nm);
        if (integer == null) {
            return new Color(def);
        }
        return new Color(integer.intValue());
    }

    public static Color getColor(String nm) {
        Integer integer = Integer.getInteger(nm);
        if (integer == null) {
            return null;
        }
        return new Color(integer.intValue());
    }

    public static Color decode(String nm) throws NumberFormatException {
        return new Color(Integer.decode(nm).intValue());
    }

    public static Color getHSBColor(float h, float s, float b) {
        return new Color(HSBtoRGB(h, s, b));
    }

    public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
        float S;
        float H;
        if (hsbvals == null) {
            hsbvals = new float[MIN_SCALABLE];
        }
        int V = Math.max(b, Math.max(r, g));
        int temp = Math.min(b, Math.min(r, g));
        float B = ((float) V) / 255.0f;
        if (V == temp) {
            S = 0.0f;
            H = 0.0f;
        } else {
            S = ((float) (V - temp)) / ((float) V);
            float Cr = ((float) (V - r)) / ((float) (V - temp));
            float Cg = ((float) (V - g)) / ((float) (V - temp));
            float Cb = ((float) (V - b)) / ((float) (V - temp));
            if (r == V) {
                H = Cb - Cg;
            } else if (g == V) {
                H = (2.0f + Cr) - Cb;
            } else {
                H = (4.0f + Cg) - Cr;
            }
            H /= 6.0f;
            if (H < 0.0f) {
                H += 1.0f;
            }
        }
        hsbvals[0] = H;
        hsbvals[1] = S;
        hsbvals[2] = B;
        return hsbvals;
    }

    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        float fr;
        float fg;
        float fb;
        if (saturation != 0.0f) {
            float H = (hue - ((float) Math.floor((double) hue))) * 6.0f;
            int I = (int) Math.floor((double) H);
            float F = H - ((float) I);
            float M = brightness * (1.0f - saturation);
            float N = brightness * (1.0f - (saturation * F));
            float K = brightness * (1.0f - ((1.0f - F) * saturation));
            switch (I) {
                case 0:
                    fr = brightness;
                    fg = K;
                    fb = M;
                    break;
                case 1:
                    fr = N;
                    fg = brightness;
                    fb = M;
                    break;
                case 2:
                    fr = M;
                    fg = brightness;
                    fb = K;
                    break;
                case MIN_SCALABLE /*3*/:
                    fr = M;
                    fg = N;
                    fb = brightness;
                    break;
                case 4:
                    fr = K;
                    fg = M;
                    fb = brightness;
                    break;
                case 5:
                    fr = brightness;
                    fg = M;
                    fb = N;
                    break;
                default:
                    fg = 0.0f;
                    fb = 0.0f;
                    fr = 0.0f;
                    break;
            }
        }
        fb = brightness;
        fg = brightness;
        fr = brightness;
        return (((((int) ((((double) fr) * 255.0d) + 0.5d)) << 16) | (((int) ((((double) fg) * 255.0d) + 0.5d)) << 8)) | ((int) ((((double) fb) * 255.0d) + 0.5d))) | -16777216;
    }
}

