package com.example.texttospeech;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

public class NumberToWordsConverter {
    public TextToSpeech textToSpeech;
    public Context context;

    public NumberToWordsConverter(Context context) {
        this.context = context;
        initializeTextToSpeech();
    }

    public void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context, "Text to speech is not supported on this device.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Text-to-speech is ready
//                        speakNumber(125);
                    }
                } else {
                    Toast.makeText(context, "Text to speech initialization failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    // This is when the speech is started
                    // Can implement any UI changes to be done in here and others
                }

                @Override
                public void onDone(String utteranceId) {
                    // Speech completed
                    // Once the speech is completed what do we need to do logic can be implemented here
                }

                @Override
                public void onError(String utteranceId) {
                    // Speech error
                    Log.d("Error", "Error in recognizing the text");
                }
            });
        }
    }

    public void speakNumber(int number) {
        String numberInWords = NumberToWords.convert(number);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(numberInWords, TextToSpeech.QUEUE_FLUSH, null, "uniqueId");
        } else {
            textToSpeech.speak(numberInWords, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void speakText(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "uniqueId");
        } else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public static class NumberToWords {
        private static final String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
        private static final String[] teens = {"", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        private static final String[] tens = {"", "Ten", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

        public static String convert(int number) {
            if (number == 0) {
                return "Zero";
            }

            return convertToText(number);
        }

        private static String convertToText(int number) {
            if (number < 10) {
                return units[number];
            } else if (number < 20) {
                return teens[number - 10];
            } else if (number < 100) {
                return tens[number / 10] + convertToText(number % 10);
            } else if (number < 1000) {
                return units[number / 100] + " Hundred " + convertToText(number % 100);
            } else if (number < 10000) {
                return units[number / 1000] + " Thousand " + convertToText(number % 1000);
            } else if (number < 100000) {
                return convertToText(number / 1000) + " Thousand " + convertToText(number % 1000);
            } else {
                // You can extend this logic for larger numbers if needed
                return "Number is too large for conversion";
            }
        }
    }

    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
