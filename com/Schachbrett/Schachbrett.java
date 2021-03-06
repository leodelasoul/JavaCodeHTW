package com.Schachbrett;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

public class Schachbrett extends JFrame implements MouseListener {
    Canvas canvas;

    enum State {MARKIERT, UNMARKIERT, GREEN}

    ;
    State[][] field;              // Solte eigene Klasse sein, da field sich gut dazu eigenet Attribute zu beherbergen wie State, FieldMitte, Markiert usw.

    int [] fieldMitte;
    public Schachbrett() {
        super();
        this.setTitle("Schachbrett");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.initField();
        this.canvas = new Canvas();
        this.canvas.addMouseListener(this);
        this.getContentPane().add(this.canvas, BorderLayout.CENTER);
// NORTH, EAST, WEST geloescht
        this.getContentPane().add(this.initSouth(), BorderLayout.SOUTH);
        this.setSize(400, 400);
        this.setLocation(300, 200);
        this.setVisible(true);
    }

    private void initField() {
        this.field = new State[8][8];
        for (int row = 0; row < this.field.length; row++) {
            for (int col = 0; col < this.field[row].length; col++) {
                this.field[row][col] = State.UNMARKIERT;
            }
        }
    }

    private class Canvas extends JPanel {
        int heightRect = 0;
        int widthRect = 0;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Implementierung von JPanel aufrufen
            Graphics2D g2 = (Graphics2D) g; // Methoden von Graphics2D nutzbar
            int canvasHeight = this.getHeight();
            int canvasWidth = this.getWidth();
            this.heightRect = canvasHeight / 8;
            this.widthRect = canvasWidth / 8;
            boolean grey = true;
            for (int row = 0; row < Schachbrett.this.field.length; row++) {
                int y = row * this.heightRect; // y-Wert des linken oberen
                grey = !grey; // mit einer anderen Farbe
                for (int col = 0; col < Schachbrett.this.field[row].length; col++) {
                    int x = col * this.widthRect; // x-Wert des linken oberen
                    if (grey) {
                        g2.setColor(Color.LIGHT_GRAY);
                        grey = false;
                    } else {
                        g2.setColor(Color.WHITE);
                        grey = true;
                    }
                    if (Schachbrett.this.field[row][col] == State.GREEN) {
                        g2.setColor(Color.GREEN);
                        Schachbrett.this.field[row][col] = State.UNMARKIERT;
                    }
                    g2.fillRect(x, y, this.widthRect, this.heightRect);
                }
            }
            for (int row = 0; row < Schachbrett.this.field.length; row++) {
                int y = row * this.heightRect;
                for (int col = 0; col < Schachbrett.this.field[row].length; col++) {
                    int x = col * this.widthRect;
                    if (Schachbrett.this.field[row][col] == State.MARKIERT) {

                        drawRedDot(g2);
                        drawLines(g2);
                    }
                }
            }
        }
    }

    private JPanel initSouth() {
        JPanel south = new JPanel();
        JButton clearBtn = new JButton("clear field");
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Schachbrett.this.initField();
                Schachbrett.this.canvas.repaint();
            }
        });
        south.add(clearBtn);
        return south;
    }

    public static void main(String[] args) {
        new Schachbrett();
    }

    /*
    Ein Rechteck wird  mit 2 Seiten definiert, einmal a Seite(von links nach rechts) und b Seite(von oben nach Unten)

    @param das angeklickte Feld wo gezecihnet werden soll, aufgeteilt in Spalte und Zeile
    setzt this.fieldMitte mit int Array mit Koordinate f??r die Mitte des Feldes
     */
    public void setFieldMitte(int zeile, int spalte) {
        int x = Math.abs((this.canvas.widthRect * (spalte+1)) - (this.canvas.widthRect/2)); //multiplizierte x-Strecke des
        // geklickten Fields minus der breite EINES fields (a Seite)
        int y = Math.abs((this.canvas.heightRect * (zeile+1)) - (this.canvas.heightRect/2)); //multiplizierte y-Strecke des
        // geklickten Fields minus der h??he EINES fields (b Seite)
        this.fieldMitte = new int []{x,y};
    }


    public void drawRedDot(Graphics2D g2){
        int durchmesser = canvas.widthRect/3; //skaliert
        g2.setColor(Color.RED);
        // das untere sieht jetzt bisschen kryptisch aus, aber:
        // wir hatten gesagt das man kreis mit: mitte und radius zeichnet
        // filloval aber ein rechteck macht, warum auch immer.... das hei??t feldmitte zu holen ist falscher ansatz.
        g2.fillOval(Schachbrett.this.fieldMitte[0]-durchmesser/2,Schachbrett.this.fieldMitte[1]-durchmesser/2, durchmesser, durchmesser);

    }

    /*
    wir haben die FieldMitte, welche uns hilft nun einfach die lines zu platzieren
     */
    public void drawLines(Graphics2D g2){
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(3.0f));

        //vertikale Linie
        int x1 = Schachbrett.this.fieldMitte[0];
        int y1 = 0;
        int x2 = Schachbrett.this.fieldMitte[0];
        int y2 = canvas.getHeight();
        g2.drawLine(x1,y1,x2,y2);

        //horizontale Linie
        int x11 = 0;
        int y11 = Schachbrett.this.fieldMitte[1];
        int x22 = canvas.getWidth();
        int y22 = Schachbrett.this.fieldMitte[1];
        g2.drawLine(x11,y11,x22,y22);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        int spalte = x / this.canvas.widthRect;
        int zeile = y / this.canvas.heightRect;

        setFieldMitte(zeile,spalte); // Notwendige Methode zum setten der FieldMitte bei jedem Klick

        boolean bereitsMarkiert = false;
        if (Schachbrett.this.field[zeile][spalte] == State.MARKIERT) {
            bereitsMarkiert = true;
        }
        for (int row = 0; row < Schachbrett.this.field.length && !bereitsMarkiert; row++) {
            if (Schachbrett.this.field[row][spalte] == State.MARKIERT) {
                bereitsMarkiert = true;
            }
        }
        for (int col = 0; col < Schachbrett.this.field[zeile].length && !bereitsMarkiert;
             col++) {
            if (Schachbrett.this.field[zeile][col] == State.MARKIERT) {
                bereitsMarkiert = true;
            }
        }
        for (int row = 0; row < Schachbrett.this.field.length && !bereitsMarkiert; row++) {
            for (int col = 0; col < Schachbrett.this.field[row].length && !bereitsMarkiert;
                 col++) {
                if (!(col == spalte && row == zeile)) // nicht das Feld selbst betrachten
                {
                    if ((Math.abs(col - spalte) == Math.abs(row - zeile)) &&
                            Schachbrett.this.field[row][col] == State.MARKIERT) {
                        bereitsMarkiert = true;
                    }
                }
            }
        }
        if (!bereitsMarkiert) {
            this.field[zeile][spalte] = State.MARKIERT;
        } else {
            this.field[zeile][spalte] = State.GREEN;
        }
        this.canvas.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
