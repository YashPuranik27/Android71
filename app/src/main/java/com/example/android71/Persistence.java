// done
package com.example.android71;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Persistence implements Serializable {
	// I added transient modifier to Bitmap since we aren't saving saving this value to this file. Not sure if this is 100% correct so we may need to remove it.
	private transient Bitmap bitmap;

	public Persistence(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			oos.writeInt(byteArray.length);
			oos.write(byteArray);
		}
	}

	private void readObject(ObjectInputStream ois) throws IOException {
		int bufferLength = ois.readInt();
		byte[] byteArray = new byte[bufferLength];
		ois.readFully(byteArray);
		bitmap = BitmapFactory.decodeByteArray(byteArray, 0, bufferLength);
	}
}
