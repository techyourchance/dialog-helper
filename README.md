# DialogHelper

Lightweight Android library that simplifies management of DialogFragments.

## Install

To use DialogHelper in your project, add this line to your Gradle dependencies configuration:

```
implementation 'com.techyourchance:dialoghelper:0.8.0'
```

## Usage

Show an instance of MyDialogFragment (if another dialog is currently shown, it will be dismissed):

```
mDialogHelper.showDialog(MyDialogFragment.newInstance(), null);
```

Show an instance of MyDialogFragment with a specific ID:

```
mDialogHelper.showDialog(MyDialogFragment.newInstance(), DIALOG_ID);
```

Get the ID of the currently shown dialog:

```
mDialogHelper.getCurrentlyShownDialogId()
```

Get the ID assigned to a dialog from within the dialog itself:

```
mDialogHelper.getDialogId(this)
``` 

## Features

DialogHelper is "lifecycle safe". This means that if you do:

```
mDialogHelper.showDialog(MyProgressDialogFragment.newInstance(), DIALOG_ID_SOME_FLOW_PROGRESS);
```

and then your application undergoes either configuration change or full save & restore (i.e. process death), this check will succeed afterwards:

```
@Override
protected void onStart() {
    super.onStart();
    if (DIALOG_ID_SOME_FLOW_PROGRESS.equals(mDialogHelper.getCurrentlyShownDialogId())) {
        // check the status of the flow and dismiss the dialog if the flow has already completed
    }
}
```

You can make DialogHelper a global singleton, but you don't have to. It will function properly even if you'll create dedicated instances in each Activity, Fragment, etc.:

```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mDialogHelper = new DialogHelper(getSupportFragmentManager()); // Activity-specific instance
}
```

## Importnat note

Due to its internal implementation details, DialogHelper will function properly only if it manages all application's dialogs (i.e. you show all the dialogs using DialogHelper). Mixing DialogHelper with other approaches might result in partial loss of functionality.

## Sample application

To get deeper insights into DialogHelper's role, you can review [sample application's source code](https://github.com/techyourchance/dialog-helper/tree/master/sample/src/main/java/com/techyourchance/dialoghelpersample).

Except for basic DialogHelper usage, this application shows some advanced design and architectural techniques that I find very useful in real Android projects. In addition, it demonstrates workarounds for quite a few limitations and bugs that I encountered over the years. 

## License

This project is licensed under the Apache-2.0 License - see the [LICENSE.txt](LICENSE.txt) file for details

