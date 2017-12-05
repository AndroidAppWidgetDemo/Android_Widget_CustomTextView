package com.example.android_test01;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * @author xxl
 *
 */
public class BaikeTextView extends TextView {
	private String TAG = "BaikeTextView";

	public Context mContext = null;
	public Paint mPaint = null;

	public int mTextHeight = 1080;
	public int mBaikeTextHeight = 0;
	public int mTextWidth = 1920;

	public String mText = "";
	public float mLineSpace = 15;

	public int mOffset = -2;
	public float mTextSize = 0;
	public int mTextColor = 0xffbbbbbb;
	public int mFontHeight = 0;

	public int mPaddingLeft = 0;
	public int mPaddingRight = 0;

	public BaikeTextView(Context context, AttributeSet set) {
		super(context, set);
		this.mContext = context;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mTextWidth = this.getWidth();

		setmTextHeight(this.getHeight());
		mText = this.getText().toString().trim();

		mText = getTextString(mContext, mText);

		if (mText == null || mText.equals("") == true) {
			return;
		}
		Log.i(TAG, "mTextStr: " + mText + "");
		mTextSize = this.getTextSize();
		mFontHeight = (int) mTextSize;
		mPaddingLeft = this.getPaddingLeft();
		mPaddingRight = this.getPaddingRight();
		mTextColor = this.getCurrentTextColor();

		Log.i(TAG, "mTextSize: " + mTextSize + "");
		Log.i(TAG, "mFontHeight: " + mFontHeight + "");
		Log.i(TAG, "mPaddingLeft: " + mPaddingLeft + "");
		Log.i(TAG, "mPaddingRight: " + mPaddingRight + "");

		mPaint.setTextSize(mTextSize);
		mPaint.setColor(mTextColor);

		ArrayList<LinePar> tempLineArray = getLineParList(mText);
		drawText(tempLineArray, mText, canvas);

	}

	/*
	 * Obtain the information of each row
	 */
	public ArrayList<LinePar> getLineParList(String mTextStr) {
		if (mTextStr == null || mTextStr.isEmpty() == true) {
			return null;
		}
		int tempStart = 0;
		int tempLineWidth = 0;
		int tempLineCount = 0;
		ArrayList<LinePar> tempLineArray = new ArrayList<LinePar>();

		//对字符串进行一次循环
		for (int i = 0; i < mTextStr.length(); i++) {
			char ch = mTextStr.charAt(i);
			String str = String.valueOf(ch);

			float strWidth = 0;
			if (str != null && str.isEmpty() == false) {
				strWidth = BaikeConstant.getWidthofString(str, mPaint);
			}
			/*
			 * 如果是换行符，将这一行的信息存入列表中
			 */
			if (ch == '\n' && tempStart != i) {
				tempLineCount++;
				addLinePar(tempStart, i, tempLineCount, 0, tempLineArray);

				if (i == (mTextStr.length() - 1)) {
					break;
				} else {
					tempStart = i + 1;
					tempLineWidth = 0;
				}
				continue;
			} else {
				tempLineWidth += Math.ceil(strWidth);

				if (tempLineWidth >= mTextWidth - mPaddingRight) {
					tempLineCount++;

					/*
					 * 对正常绘制时的“下一行第一个字符”进行判断，如果是“成对出现标点”的左侧半个，
					 * 对上一行的字符间距进行拉伸，或者不处理
					 */

					if (BaikeConstant.isLeftPunctuation(ch) == true) {

						Log.i(TAG, "i: " + i + "");
						Log.i(TAG,
								"the char is the left half of the punctuation");
						Log.i(TAG, "str: " + str + " ");
						/*
						 * if the char is the left half of the punctuation. Go
						 * into the next line of the current character
						 */
						i--;
						float tempWordSpaceOffset = (float) (tempLineWidth
								- Math.ceil(strWidth) - mTextWidth)
								/ (float) (i - tempStart);
						addLinePar(tempStart, i, tempLineCount,
								tempWordSpaceOffset, tempLineArray);

					} else if (BaikeConstant.isRightPunctuation(ch) == true) {

						/*
						 * 对正常绘制时的“下一行第一个字符”进行判断，如果是“成对出现标点”的右侧半个
						 */




						Log.i(TAG,
								"the char is the right half of the punctuation");
						Log.i(TAG, "str: " + str + " ");

						if (i == (mTextStr.length() - 1)) {
							addLinePar(tempStart, i, tempLineCount, 0,
									tempLineArray);
							break;
						} else {

							char nextChar = mTextStr.charAt(i + 1);
							if ((BaikeConstant.isHalfPunctuation(nextChar) == true || BaikeConstant
									.isPunctuation(nextChar) == true)
									&& BaikeConstant
									.isLeftPunctuation(nextChar) == false) {

								/*
								 * 对正常绘制时的“下一行第一个字符”进行判断，如果是“成对出现标点”的右侧半个
								 *
								 * 并且，“再下一个字符”是“英文标点”、“中文标点”、“右侧标点”
								 *
								 * 处理：讲这两个标点都放在上一行进行绘制
								 *
								 *
								 */



								String nextStr = String.valueOf(nextChar);
								float nextStrWidth = 0;
								if (nextStr != null
										&& nextStr.isEmpty() == false) {
									nextStrWidth = BaikeConstant
											.getWidthofString(nextStr, mPaint);
								}
								i++;
								float tempWordSpaceOffset = (float) (tempLineWidth
										+ Math.ceil(nextStrWidth) - mTextWidth)
										/ (float) (i - tempStart);
								addLinePar(tempStart, i, tempLineCount,
										tempWordSpaceOffset, tempLineArray);
							} else {


								/*
								 * 对正常绘制时的“下一行第一个字符”进行判断，如果是“成对出现标点”的右侧半个
								 *
								 * 并且，“再下一个字符”是“左侧标点”、非标点的字符
								 *
								 * 处理：只将右侧标点放在上一行进行绘制
								 *
								 *
								 */






								float tempWordSpaceOffset = (float) (tempLineWidth - mTextWidth)
										/ (float) (i - tempStart);

								addLinePar(tempStart, i, tempLineCount,
										tempWordSpaceOffset, tempLineArray);
							}
						}
					} else {

						/*
						 * 如果下一行的第一个字符是“单个出现的标点”和“非标点字符”
						 */


						/*
						 * if the char is not the left And Right half of the
						 * punctuation.
						 */
						if (BaikeConstant.isHalfPunctuation(ch) == true
								|| BaikeConstant.isPunctuation(ch) == true) {

							/*
							 * 如果下一行的第一个字符是“单个出现的标点”
							 * 放在上一行进行绘制
							 */

							/*
							 * If the current character is a punctuation mark,
							 * on the end of the Bank
							 */

							float tempWordSpaceOffset = (float) (tempLineWidth - mTextWidth)
									/ (float) (i - tempStart);

							addLinePar(tempStart, i, tempLineCount,
									tempWordSpaceOffset, tempLineArray);
						} else {



							/*
							 * 如果下一行的第一个字符是“非标点”
							 *
							 */


							/*
							 * If the current character is not a punctuation
							 */
							if (i >= 1) {
								char preChar = mTextStr.charAt(i - 1);

								if (BaikeConstant.isLeftPunctuation(preChar) == true) {


									/*
									 * 如果下一行的第一个字符是“非标点”
									 *
									 * 上一个字符(即结尾的字符)，是左侧标点
									 *
									 * 处理：两个字符全都放在下一行进行绘制
									 */


									String preStr = String.valueOf(preChar);

									float preStrWidth = 0;
									if (preStr != null
											&& preStr.isEmpty() == false) {
										preStrWidth = BaikeConstant
												.getWidthofString(preStr,
														mPaint);
									}

									Log.i(TAG,
											"the char is the left half of the punctuation");
									Log.i(TAG, "preChar: " + preChar + " ");

									i = i - 2;
									float tempWordSpaceOffset = (float) (tempLineWidth
											- Math.ceil(strWidth)
											- Math.ceil(preStrWidth) - mTextWidth)
											/ (float) (i - tempStart);
									addLinePar(tempStart, i, tempLineCount,
											tempWordSpaceOffset, tempLineArray);
								} else {


									/*
									 * 如果下一行的第一个字符是“非标点”
									 *
									 * 上一个字符(即结尾的字符)，是“非左侧标点”
									 *
									 * 处理：下一行的第一个字符放在下一行(即，不处理)
									 */






									i--;
									float tempWordSpaceOffset = (float) (tempLineWidth
											- Math.ceil(strWidth) - mTextWidth)
											/ (float) (i - tempStart);
									addLinePar(tempStart, i, tempLineCount,
											tempWordSpaceOffset, tempLineArray);

								}
							}
						}
					}
					if (i == (mTextStr.length() - 1)) {
						break;
					} else {
						tempStart = i + 1;
						tempLineWidth = 0;
					}
					continue;
				} else {
					if (i == (mTextStr.length() - 1)) {
						tempLineCount++;
						addLinePar(tempStart, i, tempLineCount, 0,
								tempLineArray);
						break;
					}
					continue;
				}
			}
		}

		return tempLineArray;

	}
	public void addLinePar(int start, int end, int lineCount,
						   float wordSpaceOffset, ArrayList<LinePar> lineList) {
		if (lineList != null) {
			LinePar linePar = new LinePar();
			linePar.setLineCount(lineCount);
			linePar.setStart(start);
			linePar.setEnd(end);
			linePar.setWordSpaceOffset(wordSpaceOffset);
			lineList.add(linePar);
		}

	}

	public void drawText(ArrayList<LinePar> tempLineArray, String mTextStr,
						 Canvas canvas) {

		if (tempLineArray == null || canvas == null || mTextStr == null
				|| mTextStr.equals("") == true) {
			return;
		}

		for (int lineNum = 0; lineNum < tempLineArray.size(); lineNum++) {

			LinePar linePar = tempLineArray.get(lineNum);
			int start = linePar.getStart();
			int end = linePar.getEnd();
			float width = linePar.getWordSpaceOffset();
			int lineCount = linePar.getLineCount();

			if (lineNum > 0 && lineNum == tempLineArray.size() - 1) {
				mBaikeTextHeight = (int) (lineCount * (mLineSpace + mTextSize));
			}
			if (start > end || end > mTextStr.length() - 1) {
				continue;
			}

			float lineWidth = 0;
			for (int strNum = start; strNum <= end; strNum++) {
				char ch = mTextStr.charAt(strNum);
				String str = String.valueOf(ch);
				if (str == null || str.equals("") == true) {
					continue;
				}
				if (ch == '\n') {
					str = "";
				}
				if (strNum > end) {
					break;
				}
				if (strNum >= start && strNum <= end && lineCount >= 1) {
					canvas.drawText(str, mPaddingLeft + lineWidth, lineCount
							* mFontHeight - mOffset + (lineCount - 1)
							* mLineSpace, mPaint);

					lineWidth += BaikeConstant.getWidthofString(str, mPaint);
					lineWidth = lineWidth - width;
				}
			}
		}
	}

	public int getBaikeTextHeight() {
		return mBaikeTextHeight;
	}

	public String getTextString(Context mContext, String mText) {
		if (mContext != null && mText != null && mText.equals("") == false) {
			return BaikeConstant.replaceTABToSpace(mText);
		}
		return "";

	}

	public void setmTextHeight(int mTextHeight) {
		this.mTextHeight = mTextHeight;
	}

	public int getmTextHeight() {
		return mTextHeight;
	}

	public class LinePar {
		private int mStart;
		private int mEnd;
		private int mLineCount;
		private float mWordSpaceOffset;

		public void setStart(int mStart) {
			this.mStart = mStart;
		}

		public void setEnd(int mEnd) {
			this.mEnd = mEnd;
		}

		public void setLineCount(int count) {
			this.mLineCount = count;
		}

		public void setWordSpaceOffset(float mWordSpaceOffset) {
			this.mWordSpaceOffset = mWordSpaceOffset;
		}

		public int getStart() {
			return mStart;
		}

		public int getEnd() {
			return mEnd;
		}

		public int getLineCount() {
			return mLineCount;
		}

		public float getWordSpaceOffset() {
			return mWordSpaceOffset;
		}
	}
}
