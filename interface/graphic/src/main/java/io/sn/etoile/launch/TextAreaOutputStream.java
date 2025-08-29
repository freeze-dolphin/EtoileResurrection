package io.sn.etoile.launch;

import javax.swing.*;
import java.io.OutputStream;

/**
* An output stream that writes its output to a javax.swing.JTextArea
* control.
*
* @author  Ranganath Kini
* @see      javax.swing.JTextArea
*/
public class TextAreaOutputStream extends OutputStream {
    private final JTextArea textControl;
    private final JScrollPane scrollPane;
    private final JScrollBar scrollBar;

    /**
     * Creates a new instance of TextAreaOutputStream which writes
     * to the specified instance of javax.swing.JTextArea control.
     *
     * @param control   A reference to the javax.swing.JTextArea
     *                  control to which the output must be redirected
     *                  to.
     */
    public TextAreaOutputStream(JTextArea control, JScrollPane scrollPane) {
        this.textControl = control;
        this.scrollPane = scrollPane;
        this.scrollBar = scrollPane.getVerticalScrollBar();
    }

    /**
     * Writes the specified byte as a character to the
     * javax.swing.JTextArea.
     *
     * @param   b   The byte to be written as character to the
     *              JTextArea.
     */
    public void write( int b ) {
        // append the data as characters to the JTextArea control
        textControl.append( String.valueOf( ( char )b ) );
        textControl.setCaretPosition( textControl.getDocument().getLength() );
        scrollPane.getVerticalScrollBar().setValue( scrollBar.getMaximum() );
        textControl.paintImmediately( textControl.getBounds() );
    }
}