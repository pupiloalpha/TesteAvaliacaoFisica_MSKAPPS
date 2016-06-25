package com.msk.taf.info;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
    private String[] pastas;
    private boolean showHidden = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        String strSDCardPath = System.getenv("SECONDARY_STORAGE");
        if ((strSDCardPath == null) || strSDCardPath.length() == 0) {
            strSDCardPath = System.getenv("EXTERNAL_SDCARD_STORAGE");
        }
        if (strSDCardPath != null) {
            if (strSDCardPath.contains(":")) {
                strSDCardPath = strSDCardPath.substring(0, strSDCardPath.indexOf(":"));
            }

            File externalFilePath = new File(strSDCardPath);
            if (externalFilePath.exists() && externalFilePath.canWrite()) {
                dir = externalFilePath.getParentFile();
            } else {

                dir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS).getParentFile().getParentFile().getParentFile();
            }

        } else {

            dir = Environment.getExternalStorageDirectory();
        }

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

        pastas = names(files);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) {

            public View getView(int position, View convertView, ViewGroup parent) {

                LayoutInflater inflater = getLayoutInflater();
                View rowView = inflater.inflate(R.layout.linha_pastas, null);

                TextView tv = (TextView) rowView.findViewById(R.id.tvPasta);
                AppCompatImageView iv = (AppCompatImageView) rowView.findViewById(R.id.ivFolder);

                String str = pastas[position];
                tv.setText(str);

                if (!files.get(position).isDirectory()) {
                    iv.setImageResource(R.drawable.ic_archive);
                    iv.setColorFilter(getResources().getColor(R.color.verde));
                } else {
                    iv.setImageResource(R.drawable.ic_folder);
                    iv.setColorFilter(getResources().getColor(R.color.cinza));
                }

                return rowView;
            }

            @Override
            public int getCount() {
                return files.size();
            }

            @Override

            public long getItemId(int posicao) {
                return posicao;
            }

        };
        lv.setAdapter(adapter);

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
                    if (path.endsWith(".xls")) {
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

        Intent result = new Intent();
        result.putExtra(CHOSEN_DIRECTORY, path);
        setResult(RESULT_OK, result);
        finish();
    }

    public ArrayList<File> filter(File[] file_list, boolean showHidden) {
        ArrayList<File> pastas = new ArrayList<File>();
        for (File file : file_list) {
            if (!file.isDirectory())
                continue;
            if (!showHidden && file.isHidden())
                continue;
            if (!file.canRead())
                continue;
            pastas.add(file);
        }
        Collections.sort(pastas);

        ArrayList<File> xls = new ArrayList<File>();
        for (File file : file_list) {
            if (file.isDirectory())
                continue;
            if (!file.isDirectory() && !file.getAbsolutePath().endsWith(".xls"))
                continue;
            if (!showHidden && file.isHidden())
                continue;
            if (!file.canRead())
                continue;
            xls.add(file);
        }
        Collections.sort(xls);

        ArrayList<File> files = new ArrayList<File>();
        for (int i = 0; i < pastas.size(); i++) {
            files.add(pastas.get(i));
        }
        for (int j = 0; j < xls.size(); j++) {
            files.add(xls.get(j));
        }
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
