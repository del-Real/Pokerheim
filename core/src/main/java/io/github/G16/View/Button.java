package io.github.G16.View;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Button extends Actor {
    private ShapeRenderer shapeRenderer;
    private Color color;
    private Runnable onClick;
    private Label label; // Aggiungi la Label per il testo
    private BitmapFont font;

    public Button(int x, int y, int width, int height, Color color, String text, Runnable onClick) {
        this.shapeRenderer = new ShapeRenderer();
        this.color = color;
        this.onClick = onClick;
        this.font = new BitmapFont();

        // Crea la Label e imposta il testo sopra il pulsante
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK); // Puoi cambiare il colore del testo se vuoi
        this.label = new Label(text, labelStyle);
        this.label.setPosition(x + (width - label.getWidth()) / 2, y + (height - label.getHeight()) / 2); // Centra la Label nel pulsante

        // Imposta le dimensioni del pulsante
        this.setBounds(x, y, width, height);

        // Aggiungi il listener per il click
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onClick != null) {
                    onClick.run();
                }
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Prima disegna il rettangolo con ShapeRenderer
        batch.end(); // Chiudi il batch per usare ShapeRenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
        shapeRenderer.end();
        batch.begin(); // Riavvia il batch per disegnare la Label

        // Poi disegna la Label sopra il pulsante
        label.draw(batch, parentAlpha);
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}