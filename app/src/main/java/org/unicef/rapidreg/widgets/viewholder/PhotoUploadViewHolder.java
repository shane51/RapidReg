package org.unicef.rapidreg.widgets.viewholder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.media.CasePhotoAdapter;
import org.unicef.rapidreg.childcase.media.CasePhotoViewActivity;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.cache.CasePhotoCache;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoUploadViewHolder extends BaseViewHolder<CaseField> {
    public static final String TAG = PhotoUploadViewHolder.class.getSimpleName();
    public static final int REQUEST_CODE_GALLERY = 1;
    public static final int REQUEST_CODE_CAMERA = 2;

    @BindView(R.id.photo_grid)
    GridView photoGrid;

    @BindView(R.id.no_photo_promote_view)
    TextView noPhotoPromoteView;

    private CaseActivity activity;

    public PhotoUploadViewHolder(Context context, View itemView) {
        super(context, itemView);
        ButterKnife.bind(this, itemView);
        activity = (CaseActivity) context;
    }

    @Override
    public void setValue(CaseField field) {
        List<Bitmap> previousPhotos = CasePhotoCache.getPhotosBits();
        setOnItemClickListenerOnViewPage();

        if (CasePhotoCache.isFull()) {
            photoGrid.setAdapter(new CasePhotoAdapter(context, previousPhotos));
            return;
        }
        appendAddPhotoIconExceptViewPage(previousPhotos);
        photoGrid.setAdapter(new CasePhotoAdapter(context, previousPhotos));
    }

    private void appendAddPhotoIconExceptViewPage(List<Bitmap> previousPhotos) {
        if (!activity.getCurrentFeature().isInDetailMode()) {
            int addIconId = CasePhotoCache.isEmpty() ? R.drawable.photo_camera : R.drawable.photo_add;
            Bitmap addPhotoIcon = BitmapFactory.decodeResource(context.getResources(), addIconId);
            previousPhotos.add(addPhotoIcon);
        } else if (previousPhotos.isEmpty()) {
            noPhotoPromoteView.setVisibility(View.VISIBLE);
        }
    }

    private void setOnItemClickListenerOnViewPage() {
        if (activity.getCurrentFeature().isInDetailMode()) {
            photoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    showViewPhotoDialog(position);
                }
            });
        }
    }

    @Override
    public void setOnClickListener(CaseField field) {
        photoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                boolean isAddPhotoGridClicked = (position == photoGrid.getAdapter().getCount() - 1);
                if (CasePhotoCache.isFull() || !isAddPhotoGridClicked) {
                    showViewPhotoDialog(position);
                } else {
                    showAddPhotoOptionDialog();
                }
            }
        });

        photoGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                boolean isPhotoGridClicked = (i < photoGrid.getAdapter().getCount() -
                        (CasePhotoCache.isFull() ? 0 : 1));

                if (isPhotoGridClicked) {
                    showDeletionConfirmDialog(i);
                }
                return true;
            }
        });
    }

    @Override
    protected String getResult() {
        return null;
    }

    @Override
    public void setFieldEditable(boolean editable) {

    }

    private void showViewPhotoDialog(final int position) {
        Intent intent = new Intent(activity, CasePhotoViewActivity.class);
        intent.putExtra("position", position);
        activity.startActivity(intent);
    }

    private void showAddPhotoOptionDialog() {
        final String fromCameraItem = "From Camera";
        final String fromGalleryItem = "From Gallery";
        final String cancelItem = "Cancel";
        final String[] items = {fromCameraItem, fromGalleryItem, cancelItem};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (fromCameraItem.equals(items[item])) {
                    Uri saveUri = Uri.fromFile(new File(CasePhotoCache.MEDIA_PATH_FOR_CAMERA));
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);
                    activity.startActivityForResult(intent, REQUEST_CODE_CAMERA);
                } else if (fromGalleryItem.equals(items[item])) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activity.startActivityForResult(intent, REQUEST_CODE_GALLERY);
                } else if (cancelItem.equals(items[item])) {
                    dialog.dismiss();
                }
            }
        }).show();
    }

    private void showDeletionConfirmDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Are you sure to remove this photo?");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                CasePhotoAdapter casePhotoAdapter = (CasePhotoAdapter) photoGrid.getAdapter();

                CasePhotoCache.removePhoto(casePhotoAdapter.getAllItems().get(position));
                if (CasePhotoCache.isEmpty()) {
                    casePhotoAdapter.clearItems();
                    Bitmap addPhotoIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.photo_camera);
                    casePhotoAdapter.addItem(addPhotoIcon);
                } else if (CasePhotoCache.isOneLessThanLimit()) {
                    casePhotoAdapter.removeItem(position);
                    Bitmap addPhotoIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.photo_add);
                    casePhotoAdapter.addItem(addPhotoIcon);
                } else {
                    casePhotoAdapter.removeItem(position);
                }
                casePhotoAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
