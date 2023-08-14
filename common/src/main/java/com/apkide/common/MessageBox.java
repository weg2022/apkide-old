package com.apkide.common;

import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.InputType;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MessageBox {
    private static Dialog shownDialog;

    @NonNull
    protected abstract Dialog buildDialog(@NonNull Activity activity);

    public static void showDialog(@NonNull Activity activity, MessageBox box) {
        if (box == null) return;
        if (shownDialog == null || !shownDialog.isShowing()) {
            shownDialog = box.buildDialog(activity);
            shownDialog.show();
        }
    }

    public static void hide() {
        if (shownDialog != null) {
            shownDialog.dismiss();
        }
    }

    public static boolean isShowing() {
        return shownDialog != null && shownDialog.isShowing();
    }

    public static void showError(@NonNull Activity activity, String title, @NonNull Throwable e) {
        showError(activity, title, e.getMessage());
    }

    public static void showError(@NonNull Activity activity, String title, String message) {
        showError(activity, title, message, null, null);
    }

    public static void showError(@NonNull Activity activity, final String title,
                                 @NonNull final String message,
                                 final String negativeButton,
                                 final Runnable no) {
        showDialog(activity, new MessageBox() {
            @NonNull
            @Override
            public Dialog buildDialog(@NonNull Activity activity2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton(activity2.getString(android.R.string.ok),
                                (dialog, id) -> dialog.dismiss());
                if (negativeButton != null) {
                    builder = builder.setNegativeButton(negativeButton, (dialog, which) -> {
                        dialog.dismiss();
                        no.run();
                    });
                }
                if (title != null) {
                    builder = builder.setTitle(title);
                }
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                return dialog;
            }
        });
    }

    public static void showInfo(@NonNull Activity activity, String title, @NonNull String message,
                                Runnable ok) {
        showInfo(activity, title, message, ok, null);
    }

    public static void showInfo(@NonNull Activity activity, String title, @NonNull String message,
                                Runnable ok, Runnable cancelled) {
        showInfo(activity, title, message, true, ok, cancelled);
    }

    public static void showInfo(@NonNull Activity activity, String title, @NonNull String message,
                                boolean cancelable, Runnable ok, Runnable cancelled) {
        showInfo(activity, title, message, cancelable,
                activity.getString(android.R.string.ok), ok, null, cancelled);
    }

    public static void showInfo(@NonNull Activity activity, final String title,
                                @NonNull final String message,
                                final boolean cancelable,
                                @NonNull final String okText,
                                final Runnable ok,
                                final String cancelText,
                                final Runnable cancelled) {
        showDialog(activity, new MessageBox() {
            @NonNull
            @Override
            public Dialog buildDialog(@NonNull Activity activity2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setMessage(message)
                        .setCancelable(cancelable);
                builder.setPositiveButton(okText, (dialog, id) -> {
                    dialog.dismiss();
                    if (ok != null) {
                        ok.run();
                    }
                });
                if (cancelText != null) {
                    builder.setNegativeButton(cancelText, (dialog, id) -> {
                        dialog.dismiss();
                        if (cancelled != null) {
                            cancelled.run();
                        }
                    });
                }

                builder.setOnCancelListener(dialog -> {
                    dialog.dismiss();
                    if (cancelled != null) {
                        cancelled.run();
                    }
                });
                if (title != null) {
                    builder.setTitle(title);
                }
                AlertDialog dialog = builder.create();
                if (cancelable) {
                    dialog.setCanceledOnTouchOutside(true);
                }
                return dialog;
            }
        });
    }

    public static void queryYesNo(@NonNull Activity activity, @StringRes int title,
                                  @StringRes int message, @NonNull List<String> list, Runnable yes, Runnable no) {
        StringBuilder listText = new StringBuilder("\n");
        for (String str : list) {
            listText = new StringBuilder((listText + "\n") + str);
        }
        queryYesNo(activity, activity.getString(title),
                activity.getString(message) + listText,
                activity.getString(android.R.string.no), no,
                activity.getString(android.R.string.yes), yes, null);
    }

    public static void queryYesNo(@NonNull Activity activity, @StringRes int title,
                                  @StringRes int message, Runnable yes, Runnable no) {
        queryYesNo(activity, activity.getString(title),
                activity.getString(message),
                activity.getString(android.R.string.yes),
                yes, activity.getString(android.R.string.no), no, null);
    }

    public static void queryYesNo(@NonNull Activity activity, String title, @NonNull String message,
                                  Runnable yes, Runnable no) {
        queryYesNo(activity, title, message, yes, no, null);
    }

    public static void queryYesNo(@NonNull Activity activity, String title, @NonNull String message,
                                  Runnable yes, Runnable no, Runnable cancelled) {
        queryYesNo(activity, title, message,
                activity.getString(android.R.string.yes),
                yes, activity.getString(android.R.string.no), no, cancelled);
    }

    public static void queryYesNo(@NonNull Activity activity, final String title,
                                  @NonNull final String message,
                                  @NonNull final String yesText,
                                  final Runnable yes,
                                  @NonNull final String noText,
                                  final Runnable no, final Runnable cancelled) {
        showDialog(activity, new MessageBox() {
            @NonNull
            @Override
            public Dialog buildDialog(@NonNull Activity activity2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton(yesText, (dialog, id) -> {
                            dialog.dismiss();
                            if (yes != null) {
                                yes.run();
                            }
                        }).setNegativeButton(noText, (dialog, id) -> {
                            dialog.dismiss();
                            if (no != null) {
                                no.run();
                            }
                        });

                builder.setOnCancelListener(dialog -> {
                    if (cancelled != null) {
                        cancelled.run();
                    }
                });
                if (title != null) {
                    builder.setTitle(title);
                }
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                return dialog;
            }
        });
    }

    public static void queryFromList(@NonNull Activity activity, String title,
                                     @NonNull List<String> values, @NonNull ValueRunnable<String> ok) {
        queryFromList(activity, title, values, true, ok);
    }

    public static void queryFromList(@NonNull Activity activity, final String title,
                                     @NonNull final List<String> values, final boolean cancelable,
                                     @NonNull final ValueRunnable<String> ok) {
        showDialog(activity, new MessageBox() {
            @NonNull
            @Override
            public Dialog buildDialog(@NonNull Activity activity2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setCancelable(cancelable)
                        .setItems(values.toArray(new CharSequence[0]), (dialog, which) -> {
                            dialog.dismiss();
                            ok.run(values.get(which));
                        });
                if (title != null) {
                    builder.setTitle(title);
                }
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(cancelable);
                return dialog;
            }
        });
    }

    public static void querySingleChoiceFromList(@NonNull Activity activity, final String title,
                                                 @NonNull final List<String> values,
                                                 @NonNull final String selectedValue,
                                                 @NonNull final ValueRunnable<String> ok) {
        showDialog(activity, new MessageBox() {
            @NonNull
            @Override
            public Dialog buildDialog(@NonNull Activity activity2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                int checkedItem = values.indexOf(selectedValue);
                builder.setCancelable(true)
                        .setSingleChoiceItems(values.toArray(new CharSequence[0]), checkedItem, (p1, p2) -> {

                        }).setPositiveButton(activity2.getString(android.R.string.ok),
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    SparseBooleanArray items = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
                                    if (items != null) {
                                        for (int i = 0; i < values.size(); i++) {
                                            if (items.get(i)) {
                                                ok.run(values.get(i));
                                            }
                                        }
                                    }
                                });
                if (title != null) {
                    builder.setTitle(title);
                }
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                return dialog;
            }
        });
    }

    public static void queryMultipleChoiceFromList(@NonNull Activity activity,
                                                   final String title,
                                                   @NonNull final List<String> values,
                                                   @NonNull final List<Boolean> selectedValues,
                                                   @NonNull final ValueRunnable<List<Integer>> ok) {
        showDialog(activity, new MessageBox() {
            @NonNull
            @Override
            public Dialog buildDialog(@NonNull Activity activity2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                boolean[] checkedItems = new boolean[selectedValues.size()];
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = selectedValues.get(i);
                }
                builder.setCancelable(true)
                        .setMultiChoiceItems(
                                values.toArray(new CharSequence[0]), checkedItems,
                                (dialog, which, isChecked) -> {

                                }).setPositiveButton(
                                activity2.getString(android.R.string.ok),
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    ArrayList<Integer> arrayList = new ArrayList<>();
                                    SparseBooleanArray items = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
                                    if (items != null) {
                                        for (int i = 0; i < values.size(); i++) {
                                            if (items.get(i)) {
                                                arrayList.add(i);
                                            }
                                        }
                                    }
                                    ok.run(arrayList);
                                });
                if (title != null) {
                    builder.setTitle(title);
                }
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                return dialog;
            }
        });
    }

    public static void queryIndexFromList(@NonNull Activity activity,
                                          final String title,
                                          @NonNull final List<String> values,
                                          @NonNull final ValueRunnable<Integer> ok) {
        showDialog(activity, new MessageBox() {
            @NonNull
            @Override
            public Dialog buildDialog(@NonNull Activity activity2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setCancelable(true)
                        .setItems(values.toArray(new CharSequence[0]), (dialog, which) -> {
                            dialog.dismiss();
                            ok.run(which);
                        });
                if (title != null) {
                    builder.setTitle(title);
                }
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                return dialog;
            }
        });
    }

    public static void queryIndexFromList(@NonNull Activity activity, final String title,
                                          @NonNull final List<String> values,
                                          @NonNull final String okButtonText,
                                          @NonNull final ValueRunnable<Integer> click,
                                          @NonNull final Runnable ok) {
        showDialog(activity, new MessageBox() {
            @NonNull
            @Override
            public Dialog buildDialog(@NonNull Activity activity2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setCancelable(true)
                        .setItems(values.toArray(new CharSequence[0]), (dialog, which) -> {
                            dialog.dismiss();
                            click.run(which);
                        }).setPositiveButton(okButtonText, (dialog, which) -> {
                            dialog.dismiss();
                            ok.run();
                        });
                if (title != null) {
                    builder.setTitle(title);
                }
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                return dialog;
            }
        });
    }

    public static void queryInt(@NonNull Activity activity, final String title,
                                @NonNull final String message,
                                final int oldValue,
                                @NonNull final ValueRunnable<Integer> ok) {
        showDialog(activity, new MessageBox() {
            @NonNull
            @SuppressLint("SetTextI18n")
            @Override
            public Dialog buildDialog(@NonNull final Activity activity2) {
                InputMethodManager imm = (InputMethodManager) activity2.getSystemService(Context.INPUT_METHOD_SERVICE);

                final AlertDialog[] dialog = new AlertDialog[1];
                final AppCompatEditText input = new AppCompatEditText(activity2) {
                    @Override
                    public boolean onKeyDown(int keyCode, KeyEvent event) {
                        if (keyCode == 66) {
                            return true;
                        }
                        return super.onKeyDown(keyCode, event);
                    }

                    @Override
                    public boolean onKeyUp(int keyCode, KeyEvent event) {
                        if (keyCode == 66) {
                            imm.hideSoftInputFromWindow(getWindowToken(), 0);
                            try {
                                ok.run(Integer.parseInt(requireNonNull(getText()).toString().trim()));
                            } catch (NumberFormatException ignored) {
                            }
                            dialog[0].dismiss();
                            return true;
                        }
                        return super.onKeyUp(keyCode, event);
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setView(input)
                        .setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton(activity2.getString(android.R.string.ok),
                                (dialog2, id) -> {
                                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                    try {
                                        ok.run(Integer.parseInt(requireNonNull(input.getText()).toString().trim()));
                                    } catch (NumberFormatException ignored) {
                                    }
                                }).setNegativeButton(activity2.getString(android.R.string.cancel), (dialog2, id) -> {
                            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                            dialog2.cancel();
                        });
                if (title != null) {
                    builder.setTitle(title);
                }
                dialog[0] = builder.create();
                dialog[0].setOnShowListener(dialog2 -> {
                    imm.showSoftInput(input, 1);
                    input.selectAll();
                });
                input.setText(String.valueOf(oldValue));
                input.setImeOptions(268435456);
                input.setInputType(2);
                input.setTypeface(Typeface.DEFAULT);
                input.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == 6) {
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                        dialog[0].dismiss();
                        try {
                            ok.run(Integer.parseInt(requireNonNull(input.getText()).toString().trim()));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    return false;
                });
                dialog[0].setCanceledOnTouchOutside(true);
                return dialog[0];
            }
        });
    }

    public static void queryPassword(@NonNull Activity activity, String title,
                                     @NonNull String message, @NonNull ValueRunnable<String> ok,
                                     Runnable cancelled) {
        queryText(activity, title, message, null, "", true, ok, cancelled, null);
    }

    public static void queryPassword(@NonNull Activity activity, String title,
                                     @NonNull String message, @NonNull ValueRunnable<String> ok) {
        queryText(activity, title, message, null, "", true, ok, null, null);
    }

    public static void queryText(@NonNull Activity activity, String title,
                                 @NonNull String message, String oldText,
                                 @NonNull ValueRunnable<String> ok, Runnable cancelled) {
        queryText(activity, title, message, null, oldText, false, ok, cancelled, null);
    }

    public static void queryText(@NonNull Activity activity, int title, int message,
                                 @NonNull String oldText, @NonNull ValueRunnable<String> ok) {
        queryText(activity, activity.getString(title), activity.getString(message), null, oldText, false, ok, null, null);
    }

    public static void queryText(@NonNull Activity activity, String title,
                                 @NonNull String message, @NonNull String oldText,
                                 @NonNull ValueRunnable<String> ok) {
        queryText(activity, title, message, null, oldText, false, ok, null, null);
    }

    public static void queryText(@NonNull Activity activity, String title,
                                 @NonNull String message, String neutralText,
                                 @NonNull String oldText, @NonNull ValueRunnable<String> ok,
                                 Runnable neutral) {
        queryText(activity, title, message, neutralText, oldText, false, ok, null, neutral);
    }

    private static void queryText(@NonNull Activity activity, final String title,
                                  @NonNull final String message,
                                  final String neutralText,
                                  @NonNull final String oldText,
                                  final boolean isPassword,
                                  @NonNull final ValueRunnable<String> ok,
                                  final Runnable cancelled,
                                  final Runnable neutral) {
        showDialog(activity, new MessageBox() {
            @NonNull
            @Override
            public Dialog buildDialog(@NonNull final Activity activity2) {
                InputMethodManager imm = (InputMethodManager) activity2.getSystemService(Context.INPUT_METHOD_SERVICE);

                final AlertDialog[] dialog = new AlertDialog[1];
                final AppCompatEditText input = new AppCompatEditText(activity2) {

                    @Override
                    public boolean onKeyDown(int keyCode, KeyEvent event) {
                        if (keyCode == 66) {
                            return true;
                        }
                        return super.onKeyDown(keyCode, event);
                    }

                    @Override
                    public boolean onKeyUp(int keyCode, KeyEvent event) {
                        if (keyCode == 66) {
                            imm.hideSoftInputFromWindow(getWindowToken(), 0);
                            dialog[0].dismiss();
                            ok.run(requireNonNull(getText()).toString().trim());
                            return true;
                        }
                        return super.onKeyUp(keyCode, event);
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(activity2);
                builder.setView(input).setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton(activity2.getString(android.R.string.ok),
                                (dialog2, id) -> {
                                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                    dialog2.dismiss();
                                    ok.run(requireNonNull(input.getText()).toString().trim());
                                }).setNegativeButton(activity2.getString(android.R.string.cancel), (dialog2, id) -> {
                            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                            dialog2.cancel();
                        });
                if (neutralText != null) {
                    builder.setNeutralButton(neutralText, (dialog2, which) -> {
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                        neutral.run();
                    });
                }
                if (title != null) {
                    builder.setTitle(title);
                }

                builder.setOnCancelListener(dialog2 -> {
                    if (cancelled != null) {
                        cancelled.run();
                    }
                });
                dialog[0] = builder.create();
                dialog[0].setOnShowListener(dialog2 -> {
                    imm.showSoftInput(input, 1);
                    input.selectAll();
                });
                input.setText(oldText);
                input.setImeOptions(268435456);
                if (isPassword) {
                    input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    input.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                input.setTypeface(Typeface.DEFAULT);
                input.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == 6) {
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                        dialog[0].dismiss();
                        ok.run(requireNonNull(input.getText()).toString().trim());
                    }
                    return false;
                });
                dialog[0].setCanceledOnTouchOutside(true);
                return dialog[0];
            }
        });
    }


    public static void queryMultipleValues(@NonNull Activity activity, String title,
                                           @NonNull final List<String> values,
                                           @NonNull List<String> displayValues,
                                           String value,
                                           @NonNull final ValueRunnable<String> ok) {
        List<Boolean> isSelected = new ArrayList<>();
        List<String> selectedValues = value == null ? new ArrayList<>() : Arrays.asList(value.split("\\|"));
        for (String v : values) {
            isSelected.add(selectedValues.contains(v));
        }

        queryMultipleChoiceFromList(activity, title, displayValues, isSelected, t -> {
            if (t.size() == 0) {
                ok.run(null);
                return;
            }
            StringBuilder newValue = new StringBuilder();
            for (Integer num : t) {
                int i = num;
                if (newValue.length() > 0) {
                    newValue.append("|");
                }
                newValue.append(values.get(i));
            }
            ok.run(newValue.toString());
        });
    }
}
