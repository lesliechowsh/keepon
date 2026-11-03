package dev.local.keepon;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

/**
 * The one control: a 220dp round power button. OFF is a quiet ring, ON is a
 * lit amber disc with a halo — readable across the room, the same glyph the
 * launcher icon draws. Pure Canvas, no drawables.
 */
final class PowerToggleView extends View {
  private static final int AMBER = 0xFFFFB13D;

  private boolean on;
  private float scale = 1f;
  private Listener listener;
  private final Paint ring = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Paint disc = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Paint glyph = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final RectF arcBox = new RectF();

  interface Listener {
    void onToggle();
  }

  PowerToggleView(Context c) {
    super(c);
    ring.setStyle(Paint.Style.STROKE);
    glyph.setStyle(Paint.Style.STROKE);
    setContentDescription("开启或关闭常亮");
  }

  void setOn(boolean v) {
    on = v;
    invalidate();
  }

  void setListener(Listener l) {
    listener = l;
  }

  @Override
  protected void onMeasure(int widthSpec, int heightSpec) {
    int size = Math.round(220f * getResources().getDisplayMetrics().density);
    setMeasuredDimension(size, size);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    float w = getWidth();
    float cx = w / 2f;
    float cy = w / 2f;
    canvas.save();
    canvas.scale(scale, scale, cx, cy);

    float rOuter = w / 2f - w * 0.05f;
    float ringW = w * 0.026f;
    ring.setStrokeWidth(ringW);
    if (on) {
      ring.setColor(0x38FFB13D);
      canvas.drawCircle(cx, cy, rOuter, ring);
      disc.setColor(AMBER);
      canvas.drawCircle(cx, cy, rOuter - ringW * 2.4f, disc);
    } else {
      ring.setColor(0x59A8A8B8);
      canvas.drawCircle(cx, cy, rOuter, ring);
    }

    float gr = rOuter * 0.44f;
    glyph.setStrokeWidth(w * 0.06f);
    glyph.setStrokeCap(Paint.Cap.ROUND);
    glyph.setColor(on ? 0xFF221604 : 0x99A8A8B8);
    arcBox.set(cx - gr, cy - gr, cx + gr, cy + gr);
    canvas.drawArc(arcBox, -90 + 33, 360 - 66, false, glyph);
    canvas.drawLine(cx, cy - gr * 1.22f, cx, cy - gr * 0.05f, glyph);

    canvas.restore();
  }

  private void pressTo(float target) {
    ValueAnimator a = ValueAnimator.ofFloat(scale, target);
    a.setDuration(110);
    a.addUpdateListener(an -> {
      scale = (float) an.getAnimatedValue();
      invalidate();
    });
    a.start();
  }

  @Override
  public boolean onTouchEvent(MotionEvent e) {
    switch (e.getActionMasked()) {
      case MotionEvent.ACTION_DOWN:
        pressTo(0.94f);
        return true;
      case MotionEvent.ACTION_UP:
        pressTo(1f);
        performClick();
        float dx = e.getX() - getWidth() / 2f;
        float dy = e.getY() - getHeight() / 2f;
        if (listener != null && dx * dx + dy * dy <= (getWidth() / 2f) * (getWidth() / 2f)) {
          listener.onToggle();
        }
        return true;
      case MotionEvent.ACTION_CANCEL:
        pressTo(1f);
        return true;
      default:
        return super.onTouchEvent(e);
    }
  }

  @Override
  public boolean performClick() {
    super.performClick();
    return true;
  }
}
