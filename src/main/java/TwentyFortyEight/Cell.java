package TwentyFortyEight;

import processing.core.PApplet;
import processing.core.PConstants;

public class Cell {

    private int x;
    private int y;
    private int value;

    private float drawX, drawY;
    private float startDrawX, startDrawY;
    private int animStartTime;
    private boolean isAnimating;
    private static final int ANIMATION_DURATION = 200;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.value = 0;
        this.drawX = App.GAP + x * (App.CELLSIZE + App.GAP);
        this.drawY = App.GAP + y * (App.CELLSIZE + App.GAP);
        this.startDrawX = drawX;
        this.startDrawY = drawY;
        this.isAnimating = false;
    }

    public void place() {
        if (this.value == 0) {
            this.value = (App.random.nextInt(2) + 1) * 2;
            this.drawX = App.GAP + x * (App.CELLSIZE + App.GAP);
            this.drawY = App.GAP + y * (App.CELLSIZE + App.GAP);
            this.startDrawX = drawX;
            this.startDrawY = drawY;
            isAnimating = false;
        }
    }

    public int getValue() {
        return value;
    }

    public void setValue(int newValue) {
        this.value = newValue;
        if (this.value == 0) {
            drawX = App.GAP + x * (App.CELLSIZE + App.GAP);
            drawY = App.GAP + y * (App.CELLSIZE + App.GAP);
            isAnimating = false;
        }
    }

    public float getDrawX() {
        return drawX;
    }

    public float getDrawY() {
        return drawY;
    }

    public void setDrawPosition(float dx, float dy, PApplet app) {
        this.startDrawX = dx;
        this.startDrawY = dy;
        this.animStartTime = app.millis();
        isAnimating = true;
    }

    private void updateAnimation(PApplet app) {
        float targetX = App.GAP + x * (App.CELLSIZE + App.GAP);
        float targetY = App.GAP + y * (App.CELLSIZE + App.GAP);
        if (this.value == 0) {
            drawX = targetX;
            drawY = targetY;
            isAnimating = false;
            return;
        }
        if (isAnimating) {
            int elapsed = app.millis() - animStartTime;
            float progress = elapsed / (float) ANIMATION_DURATION;
            if (progress >= 1.0f) {
                progress = 1.0f;
                isAnimating = false;
            }
            drawX = startDrawX + progress * (targetX - startDrawX);
            drawY = startDrawY + progress * (targetY - startDrawY);
        } else {
            drawX = targetX;
            drawY = targetY;
        }
    }

    public void draw(PApplet app) {
        if (this.value > 0) {
            updateAnimation(app);
            boolean isHovered = app.mouseX > drawX && app.mouseX < (drawX + App.CELLSIZE) &&
                    app.mouseY > drawY && app.mouseY < (drawY + App.CELLSIZE);
            app.pushStyle();
            if (isHovered) {
                app.fill(232, 207, 184);
            } else {
                int[] tileColor = getColorForValue(this.value);
                app.fill(tileColor[0], tileColor[1], tileColor[2]);
            }
            app.stroke(156, 139, 124);
            app.strokeWeight(2);
            app.rect(drawX, drawY, App.CELLSIZE, App.CELLSIZE, 5);
            int[] textColor = getTextColorForValue(this.value);
            app.fill(textColor[0], textColor[1], textColor[2]);
            app.textAlign(PConstants.CENTER, PConstants.CENTER);
            app.textSize(32);
            app.text(String.valueOf(this.value), drawX + App.CELLSIZE / 2, drawY + App.CELLSIZE / 2);
            app.popStyle();
        } else {
            float staticX = App.GAP + x * (App.CELLSIZE + App.GAP);
            float staticY = App.GAP + y * (App.CELLSIZE + App.GAP);
            boolean isHovered = app.mouseX > staticX && app.mouseX < (staticX + App.CELLSIZE) &&
                    app.mouseY > staticY && app.mouseY < (staticY + App.CELLSIZE);
            if (isHovered) {
                app.pushStyle();
                app.fill(232, 207, 184);
                app.stroke(156, 139, 124);
                app.strokeWeight(2);
                app.rect(staticX, staticY, App.CELLSIZE, App.CELLSIZE, 5);
                app.popStyle();
            }
        }
    }

    private int[] getColorForValue(int value) {
        switch (value) {
            case 2:    return new int[]{237, 227, 218};
            case 4:    return new int[]{236, 224, 202};
            case 8:    return new int[]{242, 177, 121};
            case 16:   return new int[]{244, 147, 102};
            case 32:   return new int[]{244, 123, 96};
            case 64:   return new int[]{246, 93, 59};
            case 128:  return new int[]{233, 206, 119};
            case 256:  return new int[]{237, 204, 97};
            case 512:  return new int[]{239, 197, 84};
            case 1024: return new int[]{237, 197, 65};
            case 2048: return new int[]{238, 194, 45};
            default:   return new int[]{238, 194, 45};
        }
    }

    private int[] getTextColorForValue(int value) {
        if (value <= 4) {
            return new int[]{119, 110, 101};
        } else {
            return new int[]{255, 255, 255};
        }
    }
}
