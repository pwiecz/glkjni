package org.brickshadow.roboglk;

/**
 * Style constants for text output. It is entirely up to the Java frontend
 * to decide how to display text in each of these styles--style hints
 * requested by the program can be ignored--or whether to distinguish
 * between them at all.
 * <p>
 * Z-machine interpreters will probably only use a small subset of these
 * styles.
 */
public interface GlkStyle {
    /**
     * The style of normal or body text.
     */
    int Normal = 0;
    
    /**
     * Text which is emphasized.
     */
    int Emphasized = 1;
    
    /**
     * Text which has a particular arrangement of characters. It should
     * be displayed using a fixed-width font if possible.
     */
    int Preformatted = 2;
    
    /**
     * Text which introduces a large section.
     */
    int Header = 3;
    
    /**
     * Text which introduces a smaller section within a large section.
     */
    int Subheader = 4;
    
    /**
     * Text which warns of a dangerous condition, or one which the player
     * should pay attention to.
     */
    int Alert = 5;
    
    /**
     * Text which notifies of an interesting condition. 
     */
    int Note = 6;
    
    /**
     * Text which forms a quotation or otherwise abstracted text. 
     */
    int BlockQuote = 7;
    
    /**
     * Text which the player has entered.
     */
    int Input = 8;
    
    /**
     * Text with no predefined meaning.
     */
    int User1 = 9;
    
    /**
     * Text with no predefined meaning.
     */
    int User2 = 10;
}
