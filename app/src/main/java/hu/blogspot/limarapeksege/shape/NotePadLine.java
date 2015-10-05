package hu.blogspot.limarapeksege.shape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

public class NotePadLine extends EditText {

	private Rect rectangle;
	private Paint paint;

	public NotePadLine(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		rectangle = new Rect();
		paint = new Paint();
		paint.setColor(Color.GRAY);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		int height = getHeight();
		int line_height = getLineHeight();

		int count = height / line_height;

		if (getLineCount() > count)
			count = getLineCount();

		Rect r = rectangle;
		Paint p = paint;
		int baseline = getLineBounds(0, r);

		for (int i = 0; i < count; i++) {

			canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, p);
			baseline += getLineHeight();

			super.onDraw(canvas);
		}

	}
}
