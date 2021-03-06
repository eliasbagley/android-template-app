package com.rocketmade.templateapp.ui.bugreport;


import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.rocketmade.templateapp.R;

public final class BugReportDialog extends AlertDialog implements BugReportView.ReportDetailsListener {
  public interface ReportListener {
    void onBugReportSubmit(BugReportView.Report report);
  }

  private ReportListener listener;

  public BugReportDialog(Context context) {
    super(context);

    final BugReportView view =
        (BugReportView) LayoutInflater.from(context).inflate(R.layout.bugreport_view, null);
    view.setBugReportListener(this);

    setTitle("Report a bug");
    setView(view);
    setButton(Dialog.BUTTON_NEGATIVE, "Cancel", (OnClickListener) null);
    setButton(Dialog.BUTTON_POSITIVE, "Submit", (dialog, which) -> {
      if (listener != null) {
        listener.onBugReportSubmit(view.getReport());
      }
    });
  }

  public void setReportListener(ReportListener listener) {
    this.listener = listener;
  }

  @Override protected void onStart() {
    getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
  }

  @Override public void onStateChanged(boolean valid) {
    getButton(Dialog.BUTTON_POSITIVE).setEnabled(valid);
  }
}
