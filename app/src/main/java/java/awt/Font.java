package java.awt;

import java.awt.font.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.text.AttributedCharacterIterator.Attribute;

public class Font implements Serializable
 {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -4206021311591459213L;

    /**
     * The Constant PLAIN indicates font's plain style.
     */
    public static final int PLAIN = 0;

    /**
     * The Constant BOLD indicates font's bold style.
     */
    public static final int BOLD = 1;

    /**
     * The Constant ITALIC indicates font's italic style.
     */
    public static final int ITALIC = 2;

    /**
     * The Constant ROMAN_BASELINE indicated roman baseline.
     */
    public static final int ROMAN_BASELINE = 0;

    /**
     * The Constant CENTER_BASELINE indicates center baseline.
     */
    public static final int CENTER_BASELINE = 1;

    /**
     * The Constant HANGING_BASELINE indicates hanging baseline.
     */
    public static final int HANGING_BASELINE = 2;

    /**
     * The Constant TRUETYPE_FONT indicates a font resource of type TRUETYPE.
     */
    public static final int TRUETYPE_FONT = 0;

    /**
     * The Constant TYPE1_FONT indicates a font resource of type TYPE1.
     */
    public static final int TYPE1_FONT = 1;

    /**
     * The Constant LAYOUT_LEFT_TO_RIGHT indicates that text is left to right.
     */
    public static final int LAYOUT_LEFT_TO_RIGHT = 0;

    /**
     * The Constant LAYOUT_RIGHT_TO_LEFT indicates that text is right to left.
     */
    public static final int LAYOUT_RIGHT_TO_LEFT = 1;

    /**
     * The Constant LAYOUT_NO_START_CONTEXT indicates that the text in the char
     * array before the indicated start should not be examined.
     */
    public static final int LAYOUT_NO_START_CONTEXT = 2;

    /**
     * The Constant LAYOUT_NO_LIMIT_CONTEXT indicates that text in the char
     * array after the indicated limit should not be examined.
     */
    public static final int LAYOUT_NO_LIMIT_CONTEXT = 4;

    /**
     * The Constant DEFAULT_FONT.
     */
    static final Font DEFAULT_FONT = new Font("Dialog", Font.PLAIN, 12); //$NON-NLS-1$

    /**
     * The name of the Font.
     */
    protected String name;

    /**
     * The style of the Font.
     */
    protected int style;

    /**
     * The size of the Font.
     */
    protected int size;

    /**
     * The point size of the Font.
     */
    protected float pointSize;

    // Flag if the Font object transformed
    /**
     * The transformed.
     */
    private boolean transformed;

    // Set of font attributes
    /**
     * The requested attributes.
     */
    private Hashtable<Attribute, Object> fRequestedAttributes;

    // number of glyphs in this Font
    /**
     * The num glyphs.
     */
    private transient int numGlyphs = -1;

    // code for missing glyph for this Font
    /**
     * The missing glyph code.
     */
    private transient int missingGlyphCode = -1;
	/**
     * Instantiates a new Font with the specified attributes. The Font will be
     * created with default attributes if the attribute's parameter is null.
     * 
     * @param attributes
     *            the attributes to be assigned to the new Font, or null.
     */
    public Font(Map<? extends Attribute, ?> attributes) {
        Object currAttr;

        // Default values are taken from the documentation of the Font class.
        // See Font constructor, decode and getFont sections.

        this.name = "default"; //$NON-NLS-1$
        this.size = 12;
        this.pointSize = 12;
        this.style = Font.PLAIN;

        if (attributes != null) {

            fRequestedAttributes = new Hashtable<Attribute, Object>(attributes);

            currAttr = attributes.get(TextAttribute.SIZE);
            if (currAttr != null) {
                this.pointSize = ((Float)currAttr).floatValue();
                this.size = (int)Math.ceil(this.pointSize);
            }

            currAttr = attributes.get(TextAttribute.POSTURE);
            if (currAttr != null && currAttr.equals(TextAttribute.POSTURE_OBLIQUE)) {
                this.style |= Font.ITALIC;
            }

            currAttr = attributes.get(TextAttribute.WEIGHT);
            if ((currAttr != null)
				&& (((Float)currAttr).floatValue() >= (TextAttribute.WEIGHT_BOLD).floatValue())) {
                this.style |= Font.BOLD;
            }

            currAttr = attributes.get(TextAttribute.FAMILY);
            if (currAttr != null) {
                this.name = (String)currAttr;
            }

            currAttr = attributes.get(TextAttribute.TRANSFORM);
            if (currAttr != null) {
				/*
                if (currAttr instanceof TransformAttribute) {
                    this.transformed = !((TransformAttribute)currAttr).getTransform().isIdentity();
                } else
				*/
				if (currAttr instanceof AffineTransform) {
                    this.transformed = !((AffineTransform)currAttr).isIdentity();
                }
            }

        } else {
            fRequestedAttributes = new Hashtable<Attribute, Object>(5);
            
            this.transformed = false;

            fRequestedAttributes.put(TextAttribute.FAMILY, name);

            fRequestedAttributes.put(TextAttribute.SIZE, new Float(this.size));

            if ((this.style & Font.BOLD) != 0) {
                fRequestedAttributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            } else {
                fRequestedAttributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
            }
            if ((this.style & Font.ITALIC) != 0) {
                fRequestedAttributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
            } else {
                fRequestedAttributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR);
            }

        }
    }

    /**
     * Instantiates a new Font with the specified name, style and size.
     * 
     * @param name
     *            the name of font.
     * @param style
     *            the style of font.
     * @param size
     *            the size of font.
     */
    public Font(String name, int style, int size) {

        this.name = (name != null) ? name : "Default"; //$NON-NLS-1$
        this.size = (size >= 0) ? size : 0;
        this.style = (style & ~0x03) == 0 ? style : Font.PLAIN;
        this.pointSize = this.size;

        fRequestedAttributes = new Hashtable<Attribute, Object>(5);

        // fRequestedAttributes.put(TextAttribute.TRANSFORM, IDENTITY_TRANSFORM);

        this.transformed = false;

        fRequestedAttributes.put(TextAttribute.FAMILY, this.name);
        fRequestedAttributes.put(TextAttribute.SIZE, new Float(this.size));

        if ((this.style & Font.BOLD) != 0) {
            fRequestedAttributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        } else {
            fRequestedAttributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
        }
        if ((this.style & Font.ITALIC) != 0) {
            fRequestedAttributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        } else {
            fRequestedAttributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR);
        }
    }
	
}
