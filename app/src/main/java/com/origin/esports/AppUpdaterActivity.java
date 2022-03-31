package com.origin.esports;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class AppUpdaterActivity extends AppCompatActivity {

    //app
    private static final String TAG_APP_NEWVERSION = "newversion";

    private TextView forceUpdateNote;
    private final String isForceUpdate = "true";
    private Button later;
    private String latestVersion;
    private TextView newVersion;
    private Button update;
    private TextView updateDate;
    private String updatedOn;
    private TextView whatsNew;
    private String whatsNewData;
    private String newversion;
    private static final int PERMISSION_REQUEST_CODE = 200;
    public AppUpdaterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_updater);

        newversion = getIntent().getStringExtra(TAG_APP_NEWVERSION);
        whatsNewData = getIntent().getStringExtra("data");
        updateDate = (TextView) findViewById(R.id.date);
        newVersion = (TextView) findViewById(R.id.version);
        whatsNew = (TextView) findViewById(R.id.whatsnew);
        forceUpdateNote = (TextView) findViewById(R.id.forceUpdateNote);
        later = (Button) findViewById(R.id.laterButton);
        update = (Button) findViewById(R.id.updateButton);
        updateDate.setText(updatedOn);
        newVersion.setText("New Version: v"+newversion);
        whatsNew.setText(whatsNewData);
        if (isForceUpdate.equals("true")) {
            later.setVisibility(View.GONE);
            forceUpdateNote.setVisibility(View.VISIBLE);
        }

        update.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.Gcc.OriginTournaments"));
                startActivity(browserIntent);

//                if (checkPermission()) {
//                    UpdateApp atualizaApp = new UpdateApp();
//                    atualizaApp.setContext(AppUpdaterActivity.this);
//                    atualizaApp.execute("https://gcc-org.com/Downloads/origin.apk");
//                } else {
//                    requestPermission();
//                }
            }
        });
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0) {
//
//                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//                if (locationAccepted && cameraAccepted) {
//                    UpdateApp updateApp = new UpdateApp();
//                    updateApp.setContext(AppUpdaterActivity.this);
//                    updateApp.execute("https://gcc-org.com/Downloads/Origin.apk");
//                }
//            }
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    private boolean checkPermission() {
//        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
//        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
//
//        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    private void requestPermission() {
//        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//    }
//
//    public class UpdateApp extends AsyncTask<String, Integer, String> {
//        private ProgressDialog mPDialog;
//        private Context mContext;
//
//        void setContext(Activity context) {
//            mContext = context;
//            context.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mPDialog = new ProgressDialog(mContext);
//                    mPDialog.setMessage("Please wait...");
//                    mPDialog.setIndeterminate(true);
//                    mPDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                    mPDialog.setCancelable(false);
//                    mPDialog.show();
//                }
//            });
//        }
//
//        @Override
//        protected String doInBackground(String... arg0) {
//            try {
//
//                URL url = new URL(arg0[0]);
//                HttpURLConnection c = (HttpURLConnection) url.openConnection();
//                c.setRequestMethod("GET");
//                c.setDoOutput(true);
//                c.connect();
//                int lenghtOfFile = c.getContentLength();
//
//                String PATH = Objects.requireNonNull(mContext.getExternalFilesDir(null)).getAbsolutePath();
//                File file = new File(PATH);
//                boolean isCreate = file.mkdirs();
//                File outputFile = new File(file, "my_apk.apk");
//                if (outputFile.exists()) {
//                    boolean isDelete = outputFile.delete();
//                }
//                FileOutputStream fos = new FileOutputStream(outputFile);
//
//                InputStream is = c.getInputStream();
//
//                byte[] buffer = new byte[1024];
//                int len1;
//                long total = 0;
//                while ((len1 = is.read(buffer)) != -1) {
//                    total += len1;
//                    fos.write(buffer, 0, len1);
//                    publishProgress((int) ((total * 100) / lenghtOfFile));
//                }
//                fos.close();
//                is.close();
//                if (mPDialog != null)
//                    mPDialog.dismiss();
//                installApk();
//            } catch (Exception e) {
//                Log.e("UpdateAPP", "Update error! " + e.getMessage());
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            if (mPDialog != null)
//                mPDialog.show();
//
//        }
//
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//            if (mPDialog != null) {
//                mPDialog.setIndeterminate(false);
//                mPDialog.setMax(100);
//                mPDialog.setProgress(values[0]);
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            if (mPDialog != null)
//                mPDialog.dismiss();
//            if (result != null)
//                Toast.makeText(mContext, "Download error: " + result, Toast.LENGTH_LONG).show();
//            else
//                Toast.makeText(mContext, "File Downloaded", Toast.LENGTH_SHORT).show();
//        }
//
//
//        private void installApk() {
//            try {
//                String PATH = Objects.requireNonNull(mContext.getExternalFilesDir(null)).getAbsolutePath();
//                File file = new File(PATH + "/my_apk.apk");
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                if (Build.VERSION.SDK_INT >= 24) {
//                    Uri downloaded_apk = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
//                    intent.setDataAndType(downloaded_apk, "application/vnd.android.package-archive");
//                    List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//                    for (ResolveInfo resolveInfo : resInfoList) {
//                        mContext.grantUriPermission(mContext.getApplicationContext().getPackageName() + ".provider", downloaded_apk, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    }
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    startActivity(intent);
//                } else {
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
//                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                }
//                startActivity(intent);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}