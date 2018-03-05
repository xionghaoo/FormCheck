package com.zdtco.datafetch;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.zdtco.datafetch.data.AuditForm;
import com.zdtco.datafetch.data.AuditStatus;
import com.zdtco.datafetch.data.BoundMachine;
import com.zdtco.datafetch.data.CJCellValue;
import com.zdtco.datafetch.data.CJRow;
import com.zdtco.datafetch.data.ContinuousLoginInfo;
import com.zdtco.datafetch.data.FormCheckStatus;
import com.zdtco.datafetch.data.FormPaperNo;
import com.zdtco.datafetch.data.FormPostData;
import com.zdtco.datafetch.data.FormPrintData;
import com.zdtco.datafetch.data.FormStub;
import com.zdtco.datafetch.data.Formula;
import com.zdtco.datafetch.data.GeneralForm;
import com.zdtco.datafetch.data.GeneralRow;
import com.zdtco.datafetch.data.Line;
import com.zdtco.datafetch.data.Machine;
import com.zdtco.datafetch.data.MachineAndForm;
import com.zdtco.datafetch.data.MultiColumnForm;
import com.zdtco.datafetch.data.User;
import com.zdtco.datafetch.data.UserMachineBound;
import com.zdtco.datafetch.data.WorkNoInfo;
import com.zdtco.formui.InputView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by G1494458 on 2017/12/7.
 */

public abstract class Repository extends java.util.Observable {

    public static final String TAG = "XRepository";

    public static final String THREAD_MACHINE = "机台列表";
    public static final String THREAD_GENERAL_FORM = "通用表单";
    public static final String THREAD_MULTI_COL_FORM = "保养表单";
    public static final String THREAD_USER = "人员列表";
    public static final String THREAD_CJ_ROWS = "初件栏位";
    public static final String THREAD_FORMULA = "表单公式";
    public static final String THREAD_USER_MACHINE_RELATION = "人设关系";
    public static final int DOWNLOAD_SUCCESS = 0;
    public static final int DOWNLOAD_FAILURE = 1;

    public static final int MSG_MACHINE = 0;
    public static final int MSG_USER = 1;
    public static final int MSG_GENERAL_FORM = 2;
    public static final int MSG_MULTI_COL_FORM = 3;
    public static final int MSG_CJ_ROWS = 4;
    public static final int MSG_FORMULA = 5;
    public static final int MSG_USER_MACHINE_RELATION = 6;
    public static final int PROGRESS_DOWNLOAD = 10;

    private FormDB mFormDB;
    private PreferenceManager preferenceManager;

    private SingleProgressBar machineProgress;
    private SingleProgressBar generalFormProgress;
    private SingleProgressBar multiColFormProgress;
    private SingleProgressBar userProgress;
    private SingleProgressBar cjProgress;
    private SingleProgressBar formulaProgress;
    private SingleProgressBar umRelationProgress;

    private int downloadCompleteCount = 0;
    private int downloadThreadSum = 0;

    public List<FormPostData> cacheFormList = new ArrayList<>();

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int progress = msg.arg1;
//            Log.d("test", "progress: " + progress);
            switch (msg.what) {
                case MSG_MACHINE:
                    //UI更新放在UI Thread
                    updateProgress(machineProgress, progress);
                    break;
                case MSG_GENERAL_FORM:
                    updateProgress(generalFormProgress, progress);
                    break;
                case MSG_MULTI_COL_FORM:
                    updateProgress(multiColFormProgress, progress);
                    break;
                case MSG_USER:
                    updateProgress(userProgress, progress);
                    break;
                case MSG_CJ_ROWS:
                    updateProgress(cjProgress, progress);
                    break;
                case MSG_FORMULA:
                    updateProgress(formulaProgress, progress);
                    break;
                case MSG_USER_MACHINE_RELATION:
                    updateProgress(umRelationProgress, progress);
                    break;
            }
            return true;

        }
    });

    public Repository(Application app) {
        this.mFormDB = Room.databaseBuilder(app, FormDB.class,"form.db")
                .fallbackToDestructiveMigration()  //重建所有的表
//                .addMigrations(MIGRATION_2_3)
                .build();
        this.preferenceManager = PreferenceManager.getInstance(app);
    }

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE FormStub ADD COLUMN mergeIndex INT PRIMARY KEY");
        }
    };

    private void updateProgress(SingleProgressBar progressBar, int progress) {
        progressBar.progressBar.setProgress(progress);
        progressBar.progress.setText(progress + "%");
    }

    private synchronized void notifyDownloadResult(int result) {
        setChanged();
        notifyObservers(result);
    }

    private void checkDownloadStatus() {
        if (downloadCompleteCount >= downloadThreadSum) {
            notifyDownloadResult(DOWNLOAD_SUCCESS);
        }
    }

    public void downloadAll(MultiThreadDownloadView progressView) {
        downloadCompleteCount = 0;
        downloadThreadSum = progressView.getmThreadCount();
        List<String> threads = progressView.getThreads();
        if (threads.contains(THREAD_MACHINE)) {
            getAndSaveMachine(progressView);
        }
        if (threads.contains(THREAD_GENERAL_FORM)) {
            getAndSaveGeneralForm(progressView);
        }
        if (threads.contains(THREAD_MULTI_COL_FORM)) {
            getAndSaveMultiColForm(progressView);
        }
        if (threads.contains(THREAD_USER)) {
            getAndSaveUserList(progressView);
        }
        if (threads.contains(THREAD_CJ_ROWS)) {
            getAndSaveCJRows(progressView);
        }
        if (threads.contains(THREAD_FORMULA)) {
            getAndSaveFormula(progressView);
        }
        if (threads.contains(THREAD_USER_MACHINE_RELATION)) {
            getAndSaveUserMachineBound(progressView);
        }
    }

    private void getAndSaveGeneralForm(final MultiThreadDownloadView progressView) {
        if (convertToGeneralFormList() == null)
            return;
        generalFormProgress = progressView.getSingleProgressBar(THREAD_GENERAL_FORM);
        generalFormProgress.progressBar.setProgress(PROGRESS_DOWNLOAD);
        generalFormProgress.progress.setText(PROGRESS_DOWNLOAD + "%");
        convertToGeneralFormList()
                .toList()
                .flatMap(new Function<List<GeneralForm>, SingleSource<List<GeneralForm>>>() {
                    @Override
                    public SingleSource<List<GeneralForm>> apply(List<GeneralForm> generalForms) throws Exception {
                        saveGeneralFormList(generalForms);
                        return Single.just(generalForms);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<GeneralForm>>() {
                    @Override
                    public void accept(List<GeneralForm> machines) throws Exception {
                        downloadCompleteCount ++;
                        Log.d(TAG, "downloadCompleteCount: " + downloadCompleteCount);
                        checkDownloadStatus();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        updateProgress(generalFormProgress, 0);
                        notifyDownloadResult(DOWNLOAD_FAILURE);
                        showError(progressView.getContext(), "通用表单资料下载失败：" + throwable.getLocalizedMessage());
                        Log.e(TAG, "getAndSaveGeneralForm: " + throwable.getLocalizedMessage());
                    }
                });
    }

    private void getAndSaveMultiColForm(final MultiThreadDownloadView progressView) {
        if (convertToGeneralFormList() == null)
            return;
        multiColFormProgress = progressView.getSingleProgressBar(THREAD_MULTI_COL_FORM);
        multiColFormProgress.progressBar.setProgress(PROGRESS_DOWNLOAD);
        multiColFormProgress.progress.setText(PROGRESS_DOWNLOAD + "%");
        convertToMultiColFormList()
                .toList()
                .flatMap(new Function<List<MultiColumnForm>, SingleSource<List<MultiColumnForm>>>() {
                    @Override
                    public SingleSource<List<MultiColumnForm>> apply(List<MultiColumnForm> multiColFormRows) throws Exception {
                        saveMultiColFormList(multiColFormRows);
                        return Single.just(multiColFormRows);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MultiColumnForm>>() {
                    @Override
                    public void accept(List<MultiColumnForm> multiColFormRows) throws Exception {
                        downloadCompleteCount ++;
                        Log.d(TAG, "downloadCompleteCount: " + downloadCompleteCount);
                        checkDownloadStatus();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        updateProgress(multiColFormProgress, 0);
                        showError(progressView.getContext(), "保养表单资料下载失败：" + throwable.getLocalizedMessage());
                        notifyDownloadResult(DOWNLOAD_FAILURE);

                        Log.e(TAG, "getAndSaveMultiColForm: " + throwable.getLocalizedMessage());
                    }
                });
    }

    private void getAndSaveUserList(final MultiThreadDownloadView progressView) {
        if (convertToUserList() == null)
            return;
        userProgress = progressView.getSingleProgressBar(THREAD_USER);
        userProgress.progressBar.setProgress(PROGRESS_DOWNLOAD);
        userProgress.progress.setText(PROGRESS_DOWNLOAD + "%");
        convertToUserList()
                .toList()
                .flatMap(new Function<List<User>, SingleSource<List<User>>>() {
                    @Override
                    public SingleSource<List<User>> apply(List<User> users) throws Exception {
                        saveUserList(users);
                        return Single.just(users);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) throws Exception {
                        downloadCompleteCount ++;
                        Log.d(TAG, "downloadCompleteCount: " + downloadCompleteCount);
                        if (downloadCompleteCount >= downloadThreadSum) {
                            notifyDownloadResult(DOWNLOAD_SUCCESS);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        updateProgress(userProgress, 0);
                        showError(progressView.getContext(), "用户资料下载失败：" + throwable.getLocalizedMessage());
                        notifyDownloadResult(DOWNLOAD_FAILURE);

                        Log.e(TAG, "getAndSaveUserList: " + throwable.getLocalizedMessage());
                    }
                });
    }

    private void getAndSaveCJRows(final MultiThreadDownloadView progressView) {
        if (convertToCJRow() == null)
            return;
        cjProgress = progressView.getSingleProgressBar(THREAD_CJ_ROWS);
        cjProgress.progressBar.setProgress(PROGRESS_DOWNLOAD);
        cjProgress.progress.setText(PROGRESS_DOWNLOAD + "%");
        convertToCJRow()
                .toList()
                .flatMap(new Function<List<CJRow>, SingleSource<List<CJRow>>>() {
                    @Override
                    public SingleSource<List<CJRow>> apply(List<CJRow> cjRows) throws Exception {
                        saveCJRows(cjRows);
                        return Single.just(cjRows);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CJRow>>() {
                    @Override
                    public void accept(List<CJRow> cjRows) throws Exception {
                        downloadCompleteCount ++;
                        Log.d(TAG, "downloadCompleteCount: " + downloadCompleteCount);
                        if (downloadCompleteCount >= downloadThreadSum) {
                            notifyDownloadResult(DOWNLOAD_SUCCESS);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        updateProgress(cjProgress, 0);
                        showError(progressView.getContext(), "初件资料下载失败：" + throwable.getLocalizedMessage());
                        notifyDownloadResult(DOWNLOAD_FAILURE);

                        Log.e(TAG, "getAndSaveCJRows: " + throwable.getLocalizedMessage());

                    }
                });
    }

    private void saveGeneralFormList(List<GeneralForm> generalForms) {
        //资料保存
        int count = 0;
        for (GeneralForm generalForm : generalForms) {
            mFormDB.generalFormDao().insert(generalForm);
            count ++;
            int progress = Math.round((float) PROGRESS_DOWNLOAD + (float) count / (float) generalForms.size() * 90f);
            Message message = Message.obtain();
            message.what = MSG_GENERAL_FORM;
            message.arg1 = progress;
            mHandler.sendMessage(message);
        }
    }

    private void saveMultiColFormList(List<MultiColumnForm> multiColFormRows) {
        int count = 0;
        for (MultiColumnForm multiColumnForm : multiColFormRows) {
            mFormDB.multiColFormDao().insert(multiColumnForm);
            count ++;
            int progress = Math.round((float) PROGRESS_DOWNLOAD + (float) count / (float) multiColFormRows.size() * 90f);
            Message message = Message.obtain();
            message.what = MSG_MULTI_COL_FORM;
            message.arg1 = progress;
            mHandler.sendMessage(message);
        }
    }

    private void saveUserList(List<User> users) {
        int count = 0;
        for (User user : users) {
            mFormDB.userDao().insert(user);
            count ++;
            int progress = Math.round((float) PROGRESS_DOWNLOAD + (float) count / (float) users.size() * 90f);
            Message message = Message.obtain();
            message.what = MSG_USER;
            message.arg1 = progress;
            mHandler.sendMessage(message);
        }
    }

    private void saveCJRows(List<CJRow> cjRows) {
        int count = 0;
        for (CJRow cjRow : cjRows) {
            mFormDB.cjRowDao().insert(cjRow);
            count ++;

            int progress = Math.round((float) PROGRESS_DOWNLOAD + (float) count / (float) cjRows.size() * 90f);
            Message message = Message.obtain();
            message.what = MSG_CJ_ROWS;
            message.arg1 = progress;
            mHandler.sendMessage(message);
        }
    }

    //-------------------------------设备------------------------------------
    private void getAndSaveMachine(final MultiThreadDownloadView progressView) {
        if (convertToMachineList() == null)
            return;
        machineProgress = progressView.getSingleProgressBar(THREAD_MACHINE);
        machineProgress.progressBar.setProgress(PROGRESS_DOWNLOAD);
        machineProgress.progress.setText(PROGRESS_DOWNLOAD + "%");
        convertToMachineList()
                .toList()
                .flatMap(new Function<List<Machine>, Single<List<Machine>>>() {
                    @Override
                    public Single<List<Machine>> apply(List<Machine> machines) throws Exception {
                        saveMachine(machines);
                        List<MachineAndForm> machineAndForms = new ArrayList<>();
                        for (Machine m : machines) {
                            machineAndForms.addAll(m.machineAndForms);
                        }
                        saveMachineFormRelation(machineAndForms);
                        return Single.just(machines);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Machine>>() {
                    @Override
                    public void accept(List<Machine> machines) throws Exception {
                        downloadCompleteCount ++;
                        Log.d(TAG, "downloadCompleteCount: " + downloadCompleteCount);
                        checkDownloadStatus();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        updateProgress(machineProgress, 0);
                        notifyDownloadResult(DOWNLOAD_FAILURE);
                        Log.e(TAG, "getAndSaveMachine: " + throwable.getLocalizedMessage());
                    }
                });
    }

    private void saveMachine(final List<Machine> machines) {
        //资料保存
        mFormDB.runInTransaction(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                for (Machine machine : machines) {
                    mFormDB.machineDao().insert(machine);
                    count ++;
                    int progress = Math.round((float) PROGRESS_DOWNLOAD + (float) count / (float) machines.size() * 90f);
                    Message message = Message.obtain();
                    message.what = MSG_MACHINE;
                    message.arg1 = progress;
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    public Observable<Machine> getMachineListFromLocal(String id) {
        return Observable.just(id)
                .flatMap(new Function<String, ObservableSource<Machine>>() {
                    @Override
                    public ObservableSource<Machine> apply(String nfcCode) throws Exception {
                        return Observable.fromIterable(mFormDB.machineDao().findMachinesByNFCCode(nfcCode, nfcCode.toUpperCase()));
                    }
                });
    }

    public Observable<Machine> loadAllMachineFromLocal() {
        return Observable.just("")
                .flatMap(new Function<String, ObservableSource<Machine>>() {
                    @Override
                    public ObservableSource<Machine> apply(String nfcCode) throws Exception {
                        return Observable.fromIterable(mFormDB.machineDao().findAllMachines());
                    }
                });
    }

    public Observable<List<Machine>> loadMachinesFormQuery(String query) {
        return Observable.just(query)
                .flatMap(new Function<String, ObservableSource<List<Machine>>>() {
                    @Override
                    public ObservableSource<List<Machine>> apply(String s) throws Exception {
                        return Observable.just(mFormDB.machineDao().findMachinesByQuery(s));
                    }
                });
    }

    public void updateMachineTimeLimit(final String id, final long timeLimit) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFormDB.machineDao().updateTimeLimit(id, timeLimit);
            }
        }).start();
    }

    public Observable<Line> loadLines() {
        return Observable.just("")
                .flatMap(new Function<String, ObservableSource<Line>>() {
                    @Override
                    public ObservableSource<Line> apply(String nfcCode) throws Exception {
                        return Observable.fromIterable(mFormDB.machineDao().findAllLines());
                    }
                });
    }
    //-------------------------------设备------------------------------------

    public Observable<MultiColumnForm> getMultiColFromLocal(String id) {
        return Observable.just(id)
                .flatMap(new Function<String, ObservableSource<MultiColumnForm>>() {
                    @Override
                    public ObservableSource<MultiColumnForm> apply(String id) throws Exception {
                        return Observable.just(mFormDB.multiColFormDao().findMultiColFormByID(id));
                    }
                });
    }

    public Observable<GeneralForm> getGeneralFromLocal(String formID) {
        return Observable.just(formID)
                .flatMap(new Function<String, ObservableSource<GeneralForm>>() {
                    @Override
                    public ObservableSource<GeneralForm> apply(String formID) throws Exception {
                        return Observable.just(mFormDB.generalFormDao().findFormByID(formID));
                    }
                })
                .filter(new Predicate<GeneralForm>() {
                    @Override
                    public boolean test(GeneralForm generalForm) throws Exception {
                        for (final GeneralRow row : generalForm.generalRows) {
                            if (row.inputType == InputView.Companion.getTYPE_CJ_INPUT()) {
                                //初件栏位
                                getCJRowsFormLocal(row.cjRowID)
                                        .subscribe(new Consumer<CJRow>() {
                                            @Override
                                            public void accept(CJRow cjRow) throws Exception {
                                                row.cjRowDatas = cjRow.generalRows;
                                                Log.d(TAG, "cjRowDatas: " + row.cjRowDatas.size());
                                            }
                                        }, new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable throwable) throws Exception {
                                                Log.e(TAG, "getAndSaveCJRows: " + throwable.getLocalizedMessage());
                                            }
                                        });
                            }
                        }
                        return true;
                    }
                });
    }

    private Observable<CJRow> getCJRowsFormLocal(String cjId) {
        return Observable.just(cjId)
                .flatMap(new Function<String, ObservableSource<CJRow>>() {
                    @Override
                    public ObservableSource<CJRow> apply(String id) throws Exception {
                        return Observable.just(mFormDB.cjRowDao().findCJRowByID(id));
                    }
                });
    }

    public Observable<Boolean> loginAuthCheck(String workNo) {
        return Observable.just(workNo)
                .flatMap(new Function<String, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(String s) throws Exception {
                        User user = mFormDB.userDao().findUserByID(s);
                        if (user != null && user.userName != null) {
                            return Observable.just(true);
                        } else {
                            return Observable.just(false);
                        }
                    }
                });
    }

    //-------------------------------公式------------------------------------
    public void getAndSaveFormula(final MultiThreadDownloadView progressView) {
        if (convertToFormula() == null)
            return;
        formulaProgress = progressView.getSingleProgressBar(THREAD_FORMULA);
        formulaProgress.progressBar.setProgress(PROGRESS_DOWNLOAD);
        formulaProgress.progress.setText(PROGRESS_DOWNLOAD + "%");
        convertToFormula()
                .toList()
                .flatMap(new Function<List<Formula>, SingleSource<List<Formula>>>() {
                    @Override
                    public SingleSource<List<Formula>> apply(List<Formula> formulas) throws Exception {
                        saveFormula(formulas);
                        return Single.just(formulas);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Formula>>() {
                    @Override
                    public void accept(List<Formula> formulas) throws Exception {
                        downloadCompleteCount ++;
                        Log.d(TAG, "downloadCompleteCount: " + downloadCompleteCount);
                        if (downloadCompleteCount >= downloadThreadSum) {
                            notifyDownloadResult(DOWNLOAD_SUCCESS);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        updateProgress(formulaProgress, 0);
                        showError(progressView.getContext(), "公式下载失败：" + throwable.getLocalizedMessage());
                        notifyDownloadResult(DOWNLOAD_FAILURE);
                    }
                });
    }

    private void saveFormula(List<Formula> formulas) {
//        int count = 0;
        mFormDB.formulaDao().insertFormulas(formulas);
        Message message = Message.obtain();
        message.what = MSG_FORMULA;
        message.arg1 = 100;
        mHandler.sendMessage(message);
    }

    public Observable<Formula> getFormula(String formulaID) {
        return Observable.just(formulaID)
                .flatMap(new Function<String, ObservableSource<Formula>>() {
                    @Override
                    public ObservableSource<Formula> apply(String s) throws Exception {
                        return Observable.just(mFormDB.formulaDao().findFormulaByID(s));
                    }
                });
    }

    //-------------------------------公式------------------------------------


    //-------------------------------表单打印数据------------------------------------
    public Observable<Long> saveFormPrintData(final FormPrintData formStub) {
        return Observable.just(formStub)
                .flatMap(new Function<FormPrintData, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(FormPrintData printData) throws Exception {
                        return Observable.just(mFormDB.formPrintDao().insert(formStub));
                    }
                });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mFormDB.formPrintDao().insert(formStub);
//            }
//        }).start();
    }

    public Observable<FormPrintData> getFormPrintData(Pair<String, String> pair, final long printID) {
        return Observable.just(pair)
                .flatMap(new Function<Pair<String, String>, ObservableSource<FormPrintData>>() {
                    @Override
                    public ObservableSource<FormPrintData> apply(Pair<String, String> pair) throws Exception {
                        return Observable.just(mFormDB.formPrintDao().findFormPrintDataByID(pair.first, pair.second, printID));
                    }
                });
    }

    public Observable<List<FormPrintData>> getFormPrintDataList(boolean hasPost) {
        return Observable.just(hasPost)
                .flatMap(new Function<Boolean, ObservableSource<List<FormPrintData>>>() {
                    @Override
                    public ObservableSource<List<FormPrintData>> apply(Boolean hasPost) throws Exception {
                        return Observable.just(mFormDB.formPrintDao().loadAll(hasPost));
                    }
                });
    }

    public Observable<Boolean> setFormPrintDataPostStatus(final FormPrintData data) {
        return Observable.just(data)
                .flatMap(new Function<FormPrintData, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(FormPrintData printData) throws Exception {
                        mFormDB.formPrintDao().updateFormPrintRecord(printData);
                        return Observable.just(Boolean.TRUE);
                    }
                });
    }

    public void deleteFormPrintData(final FormPrintData formStub) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFormDB.formPrintDao().deleteFormPrintData(formStub);
            }
        }).start();
    }
    //-------------------------------表单打印数据------------------------------------

    //-------------------------------表单暂存记录---------------------------------
    public void temporarySaveFormRecord(final FormStub formStub) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFormDB.formStubDao().insertFormStub(formStub);
            }
        }).start();
    }

    public Observable<FormStub> getFormStubRecord(Pair<String, String> pair) {
        return Observable.just(pair)
                .flatMap(new Function<Pair<String, String>, ObservableSource<FormStub>>() {
                    @Override
                    public ObservableSource<FormStub> apply(Pair<String, String> pair) throws Exception {
                        return Observable.just(mFormDB.formStubDao().findFormStubByID(pair.first, pair.second, 0));
                    }
                });
    }

    public void deleteFormStub(final FormStub formStub) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFormDB.formStubDao().deleteFormStub(formStub);
            }
        }).start();
    }

    public Observable<FormStub> getFormStubRecordForMerge(Pair<String, String> pair, final int index) {
        return Observable.just(pair)
                .flatMap(new Function<Pair<String, String>, ObservableSource<FormStub>>() {
                    @Override
                    public ObservableSource<FormStub> apply(Pair<String, String> pair) throws Exception {
                        return Observable.just(mFormDB.formStubDao().findFormStubByID(pair.first, pair.second, index));
                    }
                });
    }

    public void deleteFormStubForMerge(final FormStub formStub) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFormDB.formStubDao().deleteFormStub(formStub);
            }
        }).start();
    }

    public Observable<List<FormStub>> loadAllFormStubRecord() {
        return Observable.just("")
                .flatMap(new Function<String, ObservableSource<List<FormStub>>>() {
                    @Override
                    public ObservableSource<List<FormStub>> apply(String s) throws Exception {
                        return Observable.just(mFormDB.formStubDao().loadAll());
                    }
                });
    }
    //-------------------------------表单暂存记录---------------------------------

    //-------------------------------表单状态记录---------------------------------
    public void saveFormCheckStatus(final FormCheckStatus checkStatus) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFormDB.formCheckStatusDao().insertStatus(checkStatus);
            }
        }).start();
    }

    public Observable<FormCheckStatus> getFormCheckStatus(final String machineID, final String formID) {
        return Observable.just("")
                .flatMap(new Function<String, ObservableSource<FormCheckStatus>>() {
                    @Override
                    public ObservableSource<FormCheckStatus> apply(String s) throws Exception {
                        FormCheckStatus status = mFormDB.formCheckStatusDao().findCheckStatusByID(machineID, formID);
                        if (status == null) {
                            return Observable.just(new FormCheckStatus(machineID, formID));
                        } else {
                            return Observable.just(status);
                        }
                    }
                });
    }

    public void deleteFormStatusRecord(final FormCheckStatus status) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFormDB.formCheckStatusDao().deleteCheckStatus(status);
            }
        }).start();
    }
    //-------------------------------表单状态记录---------------------------------

    //-------------------------------审核表单---------------------------------
    public void saveAuditForm(final AuditForm auditForm) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFormDB.auditFormDao().insert(auditForm);
            }
        }).start();
    }

    public Observable<AuditForm> loadAuditForms() {
        return Observable.just("")
                .flatMap(new Function<String, ObservableSource<AuditForm>>() {
                    @Override
                    public ObservableSource<AuditForm> apply(String s) throws Exception {
                        return Observable.fromIterable(mFormDB.auditFormDao().loadAll());
                    }
                });
    }

    public void deleteAuditRecord(final AuditForm auditForm) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFormDB.auditFormDao().delete(auditForm);
            }
        }).start();
    }
    //-------------------------------审核表单---------------------------------

    private void showError(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void boundNFCCardWithMachine(final String machineID, final String nfcCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Machine machine = mFormDB.machineDao().findMachineByID(machineID);
                machine.nfcCode = nfcCode;
                mFormDB.machineDao().updateMachineRecord(machine);
            }
        }).start();
    }

    //-------------------------------六休一限制---------------------------------
    public Observable<ContinuousLoginInfo> getContinuousLoginInfo(String workNo) {
        return Observable.just(workNo)
                .flatMap(new Function<String, ObservableSource<ContinuousLoginInfo>>() {
                    @Override
                    public ObservableSource<ContinuousLoginInfo> apply(String s) throws Exception {
                        ContinuousLoginInfo info = mFormDB.continuousLoginInfoDao().findInfoByID(s);
                        if (info == null) {
                            return Observable.just(new ContinuousLoginInfo("", 0, 0));
                        } else {
                            return Observable.just(info);
                        }
                    }
                });
    }

    public void saveContinuousLoginInfo(final ContinuousLoginInfo continuousLoginInfo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFormDB.continuousLoginInfoDao().insert(continuousLoginInfo);
            }
        }).start();
    }
    //-------------------------------六休一限制---------------------------------

    //-------------------------------机台表单对应关系----------------------------
    public void saveMachineFormRelation(final List<MachineAndForm> machineAndForms) {
        mFormDB.machineAndFormDao().insert(machineAndForms);
    }

    public Observable<List<String>> loadMachinesByForm(final String formID) {
        return getUserMachineBound("G1465499")
                .flatMap(new Function<UserMachineBound, ObservableSource<List<String>>>() {
                    @Override
                    public ObservableSource<List<String>> apply(UserMachineBound userMachineBound) throws Exception {
                        mFormDB.boundMachineDao().insertAll(userMachineBound.boundMachines);
                        List<BoundMachine> boundMachines = mFormDB.boundMachineDao().getAll();
                        List<String> machines = mFormDB.machineAndFormDao().loadMachinesByForm(formID);
                        return Observable.just(machines);
                    }
                });
    }
    //-------------------------------机台表单对应关系----------------------------

    //-------------------------------人员设备对应关系----------------------------
    public void getAndSaveUserMachineBound(final MultiThreadDownloadView progressView) {
        if (convertUserMachineBound() == null) {
            return;
        }
        umRelationProgress = progressView.getSingleProgressBar(THREAD_USER_MACHINE_RELATION);
        umRelationProgress.progressBar.setProgress(PROGRESS_DOWNLOAD);
        umRelationProgress.progress.setText(PROGRESS_DOWNLOAD + "%");

        convertUserMachineBound()
                .toList()
                .flatMap(new Function<List<UserMachineBound>, Single<List<UserMachineBound>>>() {
                    @Override
                    public Single<List<UserMachineBound>> apply(List<UserMachineBound> machines) throws Exception {
                        saveUserMachineBound(machines);
                        return Single.just(machines);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<UserMachineBound>>() {
                    @Override
                    public void accept(List<UserMachineBound> machines) throws Exception {
                        downloadCompleteCount ++;
                        Log.d(TAG, "downloadCompleteCount: " + downloadCompleteCount);
                        checkDownloadStatus();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        updateProgress(machineProgress, 0);
                        notifyDownloadResult(DOWNLOAD_FAILURE);
                        Log.e(TAG, "getAndSaveMachine: " + throwable.getLocalizedMessage());
                    }
                });
    }

    public void saveUserMachineBound(final List<UserMachineBound> userMachineBounds) {
        mFormDB.runInTransaction(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                for (UserMachineBound machine : userMachineBounds) {
                    mFormDB.userMachineBoundDao().insert(machine);
                    count ++;
                    int progress = Math.round((float) PROGRESS_DOWNLOAD + (float) count / (float) userMachineBounds.size() * 90f);
                    Message message = Message.obtain();
                    message.what = MSG_USER_MACHINE_RELATION;
                    message.arg1 = progress;
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    public Observable<UserMachineBound> getUserMachineBound(final String userWorkNo) {
        return Observable.just(userWorkNo)
                .flatMap(new Function<String, ObservableSource<UserMachineBound>>() {
                    @Override
                    public ObservableSource<UserMachineBound> apply(String s) throws Exception {
                        return Observable.just(mFormDB.userMachineBoundDao().findUserMachineBoundByID(s));
                    }
                });
    }
    //-------------------------------人员设备对应关系----------------------------

    //-------------------------------SharedPreferences---------------------------------
    public void saveLastUpdateTime(String time) {
        preferenceManager.setLastUpdateTime(time);
    }

    public String getLastUpdateTime() {
        return preferenceManager.getLastUpdateTime();
    }

    public void setStayTime(int time) {
        preferenceManager.setStayTime(time);
    }

    public int getStayTime() {
        return preferenceManager.getStayTime();
    }

    public void setContinuousLoginDays(String time) {
        preferenceManager.setContinuousLoginDays(time);
    }

    public String getContinuousLoginDays() {
        return preferenceManager.getContinuousLoginDays();
    }

    public void setUserWorkNo(String workNo) {
        preferenceManager.setUserWorkNo(workNo);
    }

    public String getUserWorkNo() {
        return preferenceManager.getUserWorkNo();
    }
    //-------------------------------SharedPreferences---------------------------------

    public abstract Observable<Machine> convertToMachineList();

    public abstract Observable<GeneralForm> convertToGeneralFormList();

    public abstract Observable<MultiColumnForm> convertToMultiColFormList();

    public abstract Observable<User> convertToUserList();

    public abstract Observable<CJRow> convertToCJRow();

    public abstract Observable<Formula> convertToFormula();

    public abstract Observable<UserMachineBound> convertUserMachineBound();

    public abstract Observable<Boolean> postFormData(String formData);

    public abstract Observable<FormPaperNo> postCJFormData(String formData);

    public abstract Observable<String> executeFormula(String formula, String parameters);

    public abstract Observable<Boolean> bindLineLimitTime(String bindData);

    /**
     * 获取初件栏位规定值
     * @param reportcode 表单号
     * @param partnum 料号
     * @param lineNo 线体
     * @return
     */
    public abstract Observable<CJCellValue> getCJCellValues(String reportcode, String partnum, String lineNo);

    /**
     * 提交审核意见
     * @param workNo
     * @param paperNo 提交表单时生成的单号
     * @param index 审核人序号
     * @param auditdate 审核年月
     * @param audittime 审核时间
     * @param judgement 审核意见
     * @param reportcode 表单号
     * @param audit 是否审核 0表示通过，1表示退件
     * @return
     */
    public abstract Observable<Boolean> postAuditJudgement(String workNo,
                                                           String paperNo,
                                                           String index,
                                                           String auditdate,
                                                           String audittime,
                                                           String judgement,
                                                           String reportcode,
                                                           String audit);

    /**
     * 获取已提交的审核意见
     * @param paperNo 单号
     * @return
     */
    public abstract Observable<AuditStatus> getAuditJudgement(String paperNo);

    public abstract Observable<WorkNoInfo> getWorkNoInfo(String nfcTag);

}
