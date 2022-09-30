package fpt.edu.appmanagement.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fpt.edu.appmanagement.DAO.ThuChiDAO;
import fpt.edu.appmanagement.R;
import fpt.edu.appmanagement.model.KhoanThuChi;
import fpt.edu.appmanagement.model.Loai;

public class KhoanChiAdapter extends BaseAdapter {
    private ArrayList<KhoanThuChi> list;
    private Context context;
    private ThuChiDAO thuChiDAO;

    public KhoanChiAdapter(ArrayList<KhoanThuChi> list, Context context, ThuChiDAO thuChiDAO) {
        this.list = list;
        this.context = context;
        this.thuChiDAO = thuChiDAO;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public static class ViewOfItemKhoanChi {
        TextView tvTen, tvTien;
        ImageView ivSua, ivXoa;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        ViewOfItemKhoanChi viewOfItem = null;
        if (view == null) {
            view = inflater.inflate(R.layout.item_khoan_chi, null);
            viewOfItem = new ViewOfItemKhoanChi();
            viewOfItem.tvTen = view.findViewById(R.id.txtTenKhoanChi);
            viewOfItem.tvTien = view.findViewById(R.id.txtTienKhoanChi);
            viewOfItem.ivSua = view.findViewById(R.id.ivSuaKhoanChi);
            viewOfItem.ivXoa = view.findViewById(R.id.ivXoaKhoanChi);
            view.setTag(viewOfItem);
        } else {
            viewOfItem = (ViewOfItemKhoanChi) view.getTag();

        }
        viewOfItem.tvTen.setText(list.get(i).getTenloai());
        viewOfItem.tvTien.setText(String.valueOf(list.get(i).getTien()));

        viewOfItem.ivXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Bạn có muốn xoá khoản chi "+ list.get(i).getTenloai());
                builder.setPositiveButton("Xoá", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        if (thuChiDAO.xoaKhoanThuChi(list.get(i))) {
                            Toast.makeText(context, "Xoá thành công !", Toast.LENGTH_SHORT).show();
                            reLoadData();
                        } else {
                            Toast.makeText(context, "Xoá không thành công!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int positon) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        viewOfItem.ivSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogSuaKhoanChi(list.get(i));
            }


        });

        return view;
    }

    private void showDialogSuaKhoanChi(KhoanThuChi khoanThuChi) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        // ÁNH XẠ
        View view = inflater.inflate(R.layout.dialog_sua_khoa_chi, null);
        EditText edtSuaTien = view.findViewById(R.id.edtSuaTienKhoanChi);
        TextView loaiThuDangChon = view.findViewById(R.id.loaiThuDangChon);
        Button btnSua = view.findViewById(R.id.btnSuaKhoanChi);
        Button btnHuy = view.findViewById(R.id.btnHuy);
        // FILL SPINNER
        Spinner spinnerLoaiThu = view.findViewById(R.id.spnSuaLoaiChi);
        ArrayList<Loai> listLoai = thuChiDAO.getDSLoaiThuChi("chi");
        SpinerAdapter adapter = new SpinerAdapter(context, listLoai);
        spinnerLoaiThu.setAdapter(adapter);
        loaiThuDangChon.setText("Loại chi cần sửa: "+ khoanThuChi.getTenloai());
        edtSuaTien.setText(String.valueOf(khoanThuChi.getTien()));
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tien = edtSuaTien.getText().toString();
                if(tien.length() == 0){
                    Toast.makeText(context, "Vui lòng nhập số tiền !", Toast.LENGTH_SHORT).show();
                }else {
                    Loai loai = (Loai) spinnerLoaiThu.getSelectedItem();
                    int maloai = loai.getMaloai();
                    khoanThuChi.setMaloai(maloai);
                    khoanThuChi.setTien(Integer.parseInt(tien));
                    if(thuChiDAO.capNhatKhoanThuChi(khoanThuChi)){
                        Toast.makeText(context, "Sửa thành công", Toast.LENGTH_SHORT).show();
                        reLoadData();
                        dialog.dismiss();
                    }else {
                        Toast.makeText(context,  "lỗi", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });



    }
    private void reLoadData() {
        list.clear();
        list = thuChiDAO.getDSKhoanThuChi("chi");
        notifyDataSetChanged();
    }
}
