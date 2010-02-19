package com.sun.jna.examples.win32.ext;

import com.sun.jna.ptr.ByReference;

public class CharByReference extends ByReference {

	public CharByReference() {
		this((char) 0);
	}

	public CharByReference(char value) {
		super(2);
		setValue(value);
	}

	public void setValue(char value) {
		getPointer().setChar(0, value);
	}

	public char getValue() {
		return getPointer().getChar(0);
	}

}