package gloomyfish.opencvdemo;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.book.datamodel.AppConstants;
import com.book.datamodel.ChapterUtils;
import com.book.datamodel.ItemDto;
import com.book.datamodel.SectionsListViewAdaptor;

import org.opencv.android.OpenCVLoader;

import gloomyfish.opencvdemo.util.PermissionsUtils;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniLoadOpenCV();
        initListView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            PermissionsUtils.checkAndRequestMorePermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSIONS,
                    new PermissionsUtils.PermissionRequestSuccessCallBack() {
                        @Override
                        public void onHasPermission() {
                            StringBuilder sb = new StringBuilder();
                            for (String str : PERMISSIONS) {
                                sb.append(str + ",");
                            }
                            String msg = String.format("%s has allowed !", sb.substring(0, sb.length() - 1));
                            Log.d(TAG, "onHasPermission: " + msg);
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionsUtils.isPermissionRequestSuccess(grantResults)) {
            StringBuilder sb = new StringBuilder();
            for (String str : PERMISSIONS) {
                sb.append(str + ",");
            }

            String msg = String.format("%s has allowed !", sb.substring(0, sb.length() - 1));
            Log.d(TAG, "onRequestPermissionsResult: " + msg);
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void iniLoadOpenCV() {
        boolean success = OpenCVLoader.initDebug();
        if (success) {
            Log.i(TAG, "OpenCV Libraries loaded...");
        } else {
            Toast.makeText(this.getApplicationContext(), "WARNING: Could not load OpenCV Libraries!", Toast.LENGTH_LONG).show();
        }
    }

    private void initListView() {
        ListView listView = (ListView) findViewById(R.id.chapter_listView);
        final SectionsListViewAdaptor commandAdaptor = new SectionsListViewAdaptor(this);
        listView.setAdapter(commandAdaptor);
        commandAdaptor.getDataModel().addAll(ChapterUtils.getChapters());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemDto dot = commandAdaptor.getDataModel().get(position);
                goSectionList(dot);
            }
        });
        commandAdaptor.notifyDataSetChanged();
    }

    private void goSectionList(ItemDto dto) {
        Intent intent = new Intent(this.getApplicationContext(), SectionsActivity.class);
        intent.putExtra(AppConstants.ITEM_KEY, dto);
        startActivity(intent);
    }
}
