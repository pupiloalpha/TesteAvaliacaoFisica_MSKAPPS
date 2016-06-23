package com.msk.taf.info;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.msk.taf.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by msk on 14/06/16.
 */

public class EscolheArquivo extends ListActivity {

    public static final String START_DIR = "startDir";
    public static final String SHOW_HIDDEN = "showHidden";
    public static final String CHOSEN_DIRECTORY = "chosenDir";
    public static final int PICK_DIRECTORY = 43522432;
    private File dir;
    private boolean showHidden = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        dir = Environment.getExternalStorageDirectory();

        if (extras != null) {
            String preferredStartDir = extras.getString(START_DIR);
            showHidden = extras.getBoolean(SHOW_HIDDEN, false);
            if (preferredStartDir != null) {
                File startDir = new File(preferredStartDir);
                if (startDir.isDirectory()) {
                    dir = startDir;
                }
            }
        }

        setContentView(R.layout.lista_pastas);
        setTheme(R.style.TemaApp);
        setTitle(dir.getAbsolutePath());
        Button btnChoose = (Button) findViewById(R.id.btnChoose);
        btnChoose.setVisibility(View.GONE);

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        if (!dir.canRead()) {
            Toast.makeText(getApplicationContext(), "Acesso negado",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        final ArrayList<File> files = filter(dir.listFiles(), showHidden);
        String[] names = names(files);
        setListAdapter(new ArrayAdapter<String>(this, R.layout.linha_pastas, names){
            @Override
            public int getCount() {
                return files.size();
            }

            @Override
            public String getItem(int position) {
                return super.getItem(position);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                convertView = getLayoutInflater().inflate(R.layout.linha_pastas, null);
                TextView tv = (TextView) convertView.findViewById(R.id.tvpasta);
                if (!files.get(position).isDirectory())
                    tv.setTextColor(getResources().getColor(R.color.azul_claro));

                return super.getView(position, convertView, parent);
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (files.get(position).isDirectory()) {
                    String path = files.get(position).getAbsolutePath();
                    Intent intent = new Intent(EscolheArquivo.this, EscolheArquivo.class);
                    intent.putExtra(EscolheArquivo.START_DIR, path);
                    intent.putExtra(EscolheArquivo.SHOW_HIDDEN, showHidden);
                    startActivityForResult(intent, PICK_DIRECTORY);
                } else {
                    String path = files.get(position).getAbsolutePath();
                    if (path.endsWith(".xls")){
                        Intent result = new Intent();
                        result.putExtra(CHOSEN_DIRECTORY, path);
                        setResult(RESULT_OK, result);
                        finish();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_DIRECTORY && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            String path = (String) extras.get(EscolheArquivo.CHOSEN_DIRECTORY);
            returnDir(path);
        }
    }

    private void returnDir(String path) {

        SharedPreferences sharedPref = getSharedPreferences("backup", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("backup", path);
        editor.commit();

        Intent result = new Intent();
        result.putExtra(CHOSEN_DIRECTORY, path);
        setResult(RESULT_OK, result);
        finish();
    }

    public ArrayList<File> filter(File[] file_list, boolean showHidden) {
        ArrayList<File> files = new ArrayList<File>();
        for (File file : file_list) {
            if (!file.isDirectory() && !file.getAbsolutePath().endsWith(".xls"))
                continue;
            if (!showHidden && file.isHidden())
                continue;
            if (!file.canRead())
                continue;
            files.add(file);
        }
        Collections.sort(files);

        return files;
    }

    public String[] names(ArrayList<File> files) {
        String[] names = new String[files.size()];
        int i = 0;
        for (File file : files) {
            names[i] = file.getName();
            i++;
        }
        return names;
    }
}
