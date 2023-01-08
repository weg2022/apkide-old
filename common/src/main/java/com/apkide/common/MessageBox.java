package com.apkide.common;

//noinspection SuspiciousImport
import static android.R.*;
import static android.content.Context.*;
import static java.lang.Integer.parseInt;
import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MessageBox {

	private static  SparseArray<Dialog> dialogs;
	private static final int MESSAGE_ID = 2023;
	private static MessageBox messageBox;
	private static Dialog dialog;

	public static void show(Activity activity, MessageBox messageBox) {
		MessageBox.messageBox = messageBox;
		if (dialog == null || !dialog.isShowing()) {
			removeDialog();
			show(activity);
		}
	}

	public static void hide() {
		removeDialog();
	}

	private static Dialog createDialog(Activity activity ) {
		if (messageBox == null) {
			return null;
		}
		dialog = messageBox.buildDialog(activity);
		messageBox = null;
		return dialog;
	}
	private static void show(Activity activity) {
		if (dialogs == null) {
			dialogs = new SparseArray<>();
		}
		Dialog md = dialogs.get(MESSAGE_ID);
		if (md == null) {
			md = createDialog(activity);
			if (md == null)
				return;
			dialogs.put(MESSAGE_ID, md);
		}

		md.setOwnerActivity(activity);
		md.show();
	}

	public static void removeDialog() {
		if (dialogs != null) {
			final Dialog md = dialogs.get(MESSAGE_ID);
			if (md != null) {
				md.dismiss();
				dialogs.remove(MESSAGE_ID);
			}
		}
	}

	public static void destroy(){
		if (dialogs != null) {
			final int numDialogs = dialogs.size();
			for (int i = 0; i < numDialogs; i++) {
				final Dialog md = dialogs.valueAt(i);
				if (md.isShowing()) {
					md.dismiss();
				}
			}
			dialogs = null;
		}
	}

	public static boolean isShowing() {
		return dialog != null && dialog.isShowing();
	}

	public static void showError(Activity activity, String title, Throwable e) {
		showError(activity, title, e.getMessage());
	}

	public static void showError(Activity activity, String title, String message) {
		showError(activity, title, message, null, null);
	}

	public static void showError(Activity activity, String title, String message, String negativeButton, Runnable no) {
		show(activity, new MessageBox() {
			@Override
			public Dialog buildDialog(Activity context) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage(message)
						.setCancelable(true)
						.setPositiveButton(string.ok, (dialog, id) -> {
						});
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

	public static void showInfo(Activity activity, String title, String message, Runnable ok) {
		showInfo(activity, title, message, ok, null);
	}

	public static void showInfo(Activity activity, String title, String message, Runnable ok, Runnable cancelled) {
		showInfo(activity, title, message, true, ok, cancelled);
	}

	public static void showInfo(Activity activity, String title, String message, boolean cancelable, Runnable ok, Runnable cancelled) {
		showInfo(activity, title, message, cancelable, activity.getString(string.ok), ok, null, cancelled);
	}

	public static void showInfo(Activity activity, String title, String message, boolean cancelable, String okText, Runnable ok, String cancelText, Runnable cancelled) {
		show(activity, new MessageBox() {
			@Override
			public Dialog buildDialog(Activity context) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

	public static void queryYesNo(Activity activity, int title, int message,  List<String> list, Runnable yes, Runnable no) {
		StringBuilder listText = new StringBuilder("\n");
		for (String s : list) {
			listText = new StringBuilder((listText + "\n") + s);
		}
		queryYesNo(activity, activity.getResources().getString(title), activity.getString(message) + listText, activity.getString(string.no), no, activity.getString(string.yes), yes, null);
	}

	public static void queryYesNo(Activity activity, int title, int message, Runnable yes, Runnable no) {
		queryYesNo(activity, activity.getString(title), activity.getString(message), activity.getString(string.yes), yes, activity.getString(string.no), no, null);
	}

	public static void queryYesNo(Activity activity, String title, String message, Runnable yes, Runnable no) {
		queryYesNo(activity, title, message, yes, no, (Runnable) null);
	}

	public static void queryYesNo(Activity activity, String title, String message, Runnable yes, Runnable no, Runnable cancelled) {
		queryYesNo(activity, title, message, activity.getString(string.yes), yes, activity.getString(string.no), no, cancelled);
	}

	public static void queryYesNo(Activity activity, String title, String message, String yesText, Runnable yes, String noText, Runnable no, Runnable cancelled) {
		show(activity, new MessageBox() {
			@Override
			public Dialog buildDialog(Activity context) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage(message)
						.setCancelable(true)
						.setPositiveButton(yesText, (dialog, id) -> {
							dialog.dismiss();
							if (yes != null) {
								yes.run();
							}
						})
						.setNegativeButton(noText, (dialog, id) -> {
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


	public static void queryFromList(Activity activity, String title, List<String> values, ValueRunnable<String> ok) {
		queryFromList(activity, title, values, true, ok);
	}

	private static void queryFromList(Activity activity, String title, List<String> values, boolean cancelable, ValueRunnable<String> ok) {
		show(activity, new MessageBox() {
			@Override
			public Dialog buildDialog(Activity context) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setCancelable(cancelable)
						.setItems((CharSequence[]) values.toArray(new CharSequence[0]), (dialog, which) -> {
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

	public static void querySingleChoiceFromList(Activity activity, String title, List<String> values, String selectedValue, ValueRunnable<String> ok) {
		show(activity, new MessageBox() {
			@Override
			public Dialog buildDialog(Activity context) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setCancelable(true)
						.setSingleChoiceItems((CharSequence[]) values.toArray(new CharSequence[0]), values.indexOf(selectedValue), (p1, p2) -> {

						})
						.setPositiveButton(string.ok, (dialog, which) -> {
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

	public static void queryMultipleChoiceFromList(Activity activity, String title, List<String> values, List<Boolean> selectedValues, ValueRunnable<List<Integer>> ok) {
		show(activity, new MessageBox() {
			@Override
			public Dialog buildDialog(Activity context) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				boolean[] checkedItems = new boolean[selectedValues.size()];
				for (int i = 0; i < checkedItems.length; i++) {
					checkedItems[i] = selectedValues.get(i);
				}
				builder.setCancelable(true)
						.setMultiChoiceItems((CharSequence[]) values.toArray(new CharSequence[0]), checkedItems, (dialog, which, isChecked) -> {
						})
						.setPositiveButton(string.ok, (dialog, which) -> {
							dialog.dismiss();
							ArrayList<Integer> arrayList = new ArrayList<>();
							SparseBooleanArray items = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
							if (items != null) {
								for (int j = 0; j < values.size(); j++) {
									if (items.get(j)) {
										arrayList.add(j);
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

	public static void queryIndexFromList(Activity activity, String title, List<String> values, ValueRunnable<Integer> ok) {
		show(activity, new MessageBox() {
			@Override
			public Dialog buildDialog(Activity context) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setCancelable(true)
						.setItems((CharSequence[]) values.toArray(new CharSequence[0]), (dialog, which) -> {
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

	public static void queryIndexFromList(Activity activity, String title, List<String> values, String okButtonText, ValueRunnable<Integer> click, Runnable ok) {
		show(activity, new MessageBox() {
			@Override
			public Dialog buildDialog(Activity context) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setCancelable(true)
						.setItems((CharSequence[]) values.toArray(new CharSequence[0]), (dialog, which) -> {
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

	public static void queryInt(Activity activity, String title, String message, int oldValue, ValueRunnable<Integer> ok) {
		show(activity, new MessageBox() {
			@SuppressLint("SetTextI18n")
			@Override
			public Dialog buildDialog(Activity context) {
				InputMethodManager inputMethodManager = ((InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE));

				AlertDialog[] dialog = new AlertDialog[1];
				EditText input = new EditText(context) {
					@Override
					public boolean onKeyDown(int keyCode, KeyEvent event) {
						if (keyCode == 66) {
							return true;
						}
						return super.onKeyDown(keyCode, event);
					}


					@Override
					public boolean onKeyUp(int keyCode, KeyEvent event) {
						if (keyCode != 66) {
							return super.onKeyUp(keyCode, event);
						}
						inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
						try {
							ok.run(parseInt(requireNonNull(getText()).toString().trim()));
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
						dialog[0].dismiss();
						return true;
					}
				};
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setView(input)
						.setMessage(message)
						.setCancelable(true)
						.setPositiveButton(string.ok, (dialogInterface, id) -> {
							inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
							try {
								ok.run(parseInt(requireNonNull(input.getText()).toString().trim()));
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}
						})
						.setNegativeButton(string.cancel, (dialog2, id) -> {
							inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
							dialog2.cancel();
						});
				if (title != null) {
					builder.setTitle(title);
				}
				dialog[0] = builder.create();
				dialog[0].setOnShowListener(dialogInterface -> {
					inputMethodManager.showSoftInput(input, 1);
					input.selectAll();
				});
				input.setText(oldValue + "");
				input.setImeOptions(268435456);
				input.setInputType(2);
				input.setTypeface(Typeface.DEFAULT);
				input.setOnEditorActionListener((v, actionId, event) -> {
					if (actionId == 6) {
						inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
						dialog[0].dismiss();
						try {
							ok.run(parseInt(requireNonNull(input.getText()).toString().trim()));
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
					return false;
				});
				dialog[0].setCanceledOnTouchOutside(true);
				return dialog[0];
			}
		});
	}

	public static void queryPassword(Activity activity, String title, String message, ValueRunnable<String> ok, Runnable cancelled) {
		queryText(activity, title, message, null, "", true, ok, cancelled, null);
	}

	public static void queryPassword(Activity activity, String title, String message, ValueRunnable<String> ok) {
		queryText(activity, title, message, null, "", true, ok, null, null);
	}

	public static void queryText(Activity activity, String title, String message, String oldText, ValueRunnable<String> ok, Runnable cancelled) {
		queryText(activity, title, message, null, oldText, false, ok, cancelled, null);
	}

	public static void queryText(Activity activity, int title, int message, String oldText, ValueRunnable<String> ok) {
		queryText(activity, activity.getResources().getString(title), activity.getResources().getString(message), null, oldText, false, ok, null, null);
	}

	public static void queryText(Activity activity, String title, String message, String oldText, ValueRunnable<String> ok) {
		queryText(activity, title, message, null, oldText, false, ok, null, null);
	}

	public static void queryText(Activity activity, String title, String message, String neutralText, String oldText, ValueRunnable<String> ok, Runnable neutral) {
		queryText(activity, title, message, neutralText, oldText, false, ok, null, neutral);
	}

	private static void queryText(Activity activity, String title, String message, String neutralText, String oldText, boolean isPassword, ValueRunnable<String> ok, Runnable cancelled, Runnable neutral) {
		show(activity, new MessageBox() {
			@Override
			public Dialog buildDialog(Activity context) {
				InputMethodManager inputMethodManager = ((InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE));

				AlertDialog[] dialog = new AlertDialog[1];
				EditText input = new EditText(context) {
					@Override
					public boolean onKeyDown(int keyCode, KeyEvent event) {
						if (keyCode == 66) {
							return true;
						}
						return super.onKeyDown(keyCode, event);
					}

					@Override
					public boolean onKeyUp(int keyCode, KeyEvent event) {
						if (keyCode != 66)
							return super.onKeyUp(keyCode, event);

						inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
						dialog[0].dismiss();
						ok.run(requireNonNull(getText()).toString().trim());
						return true;
					}
				};
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setView(input)
						.setMessage(message)
						.setCancelable(true)
						.setPositiveButton(string.ok, (dialogInterface, id) -> {
							inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
							dialogInterface.dismiss();
							ok.run(requireNonNull(input.getText()).toString().trim());
						})
						.setNegativeButton(string.cancel, (dialogInterface, id) -> {
							inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
							dialogInterface.cancel();
						});
				if (neutralText != null) {
					builder.setNeutralButton(neutralText, (dialogInterface, which) -> {
						inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
						neutral.run();
					});
				}

				if (title != null)
					builder.setTitle(title);

				builder.setOnCancelListener(dialogInterface -> {
					if (cancelled != null)
						cancelled.run();
				});
				dialog[0] = builder.create();
				dialog[0].setOnShowListener(dialog2 -> {
					inputMethodManager.showSoftInput(input, 1);
					input.selectAll();
				});
				input.setText(oldText);
				input.setImeOptions(268435456);
				if (isPassword)
					input.setInputType(129);
				else
					input.setInputType(145);

				input.setTypeface(Typeface.DEFAULT);
				input.setOnEditorActionListener((v, actionId, event) -> {
					if (actionId == 6) {
						inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
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



	public static void queryMultipleValues(Activity activity, String title, List<String> values, List<String> displayValues, String value, ValueRunnable<String> ok) {
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
				newValue.append((String) values.get(i));
			}
			ok.run(newValue.toString());
		});
	}

	protected abstract Dialog buildDialog(Activity context);
}
