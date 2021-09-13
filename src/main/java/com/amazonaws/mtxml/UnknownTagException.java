package com.amazonaws.mtxml;

public class UnknownTagException extends Exception {
	UnknownTagException(String inputTag){
		super(String.format("Unknown tag %s!", inputTag));
	}
}
