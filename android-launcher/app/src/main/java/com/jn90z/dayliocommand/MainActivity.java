package com.jn90z.dayliocommand;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.*;

public class MainActivity extends Activity {
  private static final int PICK_FILE = 5;
  private TextView status, commandView;
  private String command = "am start --user 10 -n net.daylio/net.daylio.activities.OverviewActivity";
  private static final Pattern ALLOWED = Pattern.compile("^(?:adb\\s+shell\\s+)?am\\s+start\\s+(?:--user\\s+10\\s+)?-n\\s+([a-zA-Z0-9_.]+)/([a-zA-Z0-9_.$]+)\\s*$");
  @Override public void onCreate(Bundle b) {
    super.onCreate(b);
    LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL); layout.setPadding(28, 32, 28, 20);
    TextView title = new TextView(this); title.setText("Daylio Command Launcher"); title.setTextSize(24); layout.addView(title);
    commandView = new TextView(this); commandView.setText(command); commandView.setTextIsSelectable(true); layout.addView(commandView);
    Button importButton = new Button(this); importButton.setText("Import command .txt"); layout.addView(importButton);
    Button runButton = new Button(this); runButton.setText("Launch app"); layout.addView(runButton);
    status = new TextView(this); status.setText("Local launch mode. ADB network execution is not implemented in this prototype."); layout.addView(status);
    setContentView(layout);
    importButton.setOnClickListener(v -> {
      Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT); i.setType("text/plain"); i.addCategory(Intent.CATEGORY_OPENABLE);
      startActivityForResult(i, PICK_FILE);
    });
    runButton.setOnClickListener(v -> launch());
  }
  @Override protected void onActivityResult(int request, int result, Intent data) {
    super.onActivityResult(request, result, data);
    if (request != PICK_FILE || result != RESULT_OK || data == null) return;
    try (InputStream in = getContentResolver().openInputStream(data.getData())) {
      ByteArrayOutputStream out = new ByteArrayOutputStream(); byte[] buf = new byte[1024]; int n;
      while ((n = in.read(buf)) != -1) { if (out.size() + n > 4096) throw new IOException("File exceeds 4 KB"); out.write(buf,0,n); }
      String s = out.toString("UTF-8").trim();
      if (!ALLOWED.matcher(s).matches()) throw new IOException("Only a single approved 'am start -n' command is accepted");
      command = s; commandView.setText(s); status.setText("Command imported");
    } catch (Exception e) { status.setText("Import failed: " + e.getMessage()); }
  }
  private void launch() {
    Matcher m = ALLOWED.matcher(command);
    if (!m.matches()) { status.setText("Invalid command"); return; }
    String pkg = m.group(1), cls = m.group(2);
    if (cls.startsWith(".")) cls = pkg + cls;
    if (!pkg.equals("net.daylio") || !cls.equals("net.daylio.activities.OverviewActivity")) {
      status.setText("Prototype permits only Daylio's main activity"); return;
    }
    try {
      Intent i = new Intent(); i.setClassName(pkg,cls); i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(i);
      status.setText("Android launch requested. Amazon Kids may block this.");
    } catch (Exception e) { status.setText("Launch blocked: " + e.getClass().getSimpleName() + ": " + e.getMessage()); }
  }
}
