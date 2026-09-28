package com.example.developerdirectory.service;

/** The bytes from S3 plus the type recorded when they were uploaded. */
public record ProfileImage(byte[] content, String contentType) {
}
