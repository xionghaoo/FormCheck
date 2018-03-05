package xh.formmodules;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.zdtco.FUtil;
import com.zdtco.datafetch.data.AuditStatus;
import com.zdtco.datafetch.data.CJCellValue;
import com.zdtco.datafetch.data.CJRow;
import com.zdtco.datafetch.data.FormPaperNo;
import com.zdtco.datafetch.data.Formula;
import com.zdtco.datafetch.data.GeneralForm;
import com.zdtco.datafetch.data.GeneralRow;
import com.zdtco.datafetch.data.Machine;
import com.zdtco.datafetch.Repository;
import com.zdtco.datafetch.data.MultiColDisplayCell;
import com.zdtco.datafetch.data.MultiColFormRow;
import com.zdtco.datafetch.data.MultiColumnForm;
import com.zdtco.datafetch.data.User;
import com.zdtco.datafetch.data.MachineOwnedForm;
import com.zdtco.datafetch.data.UserMachineBound;
import com.zdtco.datafetch.data.WorkNoInfo;
import com.zdtco.formui.FormView;
import com.zdtco.formui.InputView;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import xh.formmodules.data.CustomCellResult;
import xh.formmodules.data.FormulaResult;
import xh.formmodules.data.GeneralFormResult;
import xh.formmodules.data.MachineResult;
import xh.formmodules.data.MultiColFormContentResult;
import xh.formmodules.data.MultiColFormTitleResult;
import xh.formmodules.data.GeneralPostResult;
import xh.formmodules.data.UserResult;

/**
 * Created by G1494458 on 2017/12/8.
 */

public class AppRepository extends Repository {

    private static final String FACTORY = "";
    boolean TEST = false;

    ApiRequest apiRequest;
    private MultiColFormTitleResult tmpTitles;

    public AppRepository(@NonNull Application application) {
        super(application);
        apiRequest = new ApiRequest();
    }

    @Override
    public Observable<Machine> convertToMachineList() {
        return apiRequest.getMachineList(FACTORY)
                .flatMap(new Function<MachineResult, ObservableSource<MachineResult.Data>>() {
                    @Override
                    public ObservableSource<MachineResult.Data> apply(MachineResult result) throws Exception {

                        return Observable.fromIterable(result.getData());
                    }
                })
                .flatMap(new Function<MachineResult.Data, ObservableSource<Machine>>() {
                    @Override
                    public ObservableSource<Machine> apply(MachineResult.Data machineData) throws Exception {
                        Machine machine = new Machine(machineData.getEquId());

                        machine.nfcCode = machineData.getNfctag();
                        machine.machineName = machineData.getEquName();
                        machine.facName = machineData.getFactory();
                        machine.line = machineData.getLine();
                        machine.lineName = machineData.getLinename();
                        machine.classr = machineData.getClassr();
                        machine.limitTime = machineData.getLimittime();

                        long limitTime = 0;
                        try {
                            limitTime = Long.parseLong(machine.limitTime);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        for (MachineResult.Data.OwnedForms list : machineData.getOwnedForms()) {

                            int template = 0;
                            switch (list.getForm()) {
                                case "Maintenance/MachineNoScan.aspx,ReportMode1.aspx":
                                    template = FormView.Companion.getTYPE_GENERAL();
                                    break;
                                case "Maintenance/MachineNoScan.aspx,ReportMode4.aspx":
                                    template = FormView.Companion.getTYPE_FOUR();
                                    break;
                                case "Maintenance/MachineNoScan.aspx,ReportMode6.aspx":
                                    template = FormView.Companion.getTYPE_MAINTENANCE();
                                    break;
                                case "Maintenance/MachineNoScan.aspx,ReportMode5.aspx":
                                    template = FormView.Companion.getTYPE_FIVE();
                                    break;
                                case "Maintenance/MachineNoScan.aspx,ReportMode7.aspx":
                                    template = FormView.Companion.getTYPE_XJ();
                                    break;
                            }

                            int macType = -1;
                            try {
                                macType = Integer.parseInt(list.getMactype());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            MachineOwnedForm form = new MachineOwnedForm(list.getReportcode(),
                                    machine.id, list.getReportname(), template, limitTime, macType);
                            machine.machineOwnedForms.add(form);
                        }
                        return Observable.just(machine);
                    }
                });
    }

    @Override
    public Observable<GeneralForm> convertToGeneralFormList() {
        return apiRequest.getGeneralFormList(FACTORY)
                .flatMap(new Function<GeneralFormResult, ObservableSource<GeneralFormResult.Data>>() {
                    @Override
                    public ObservableSource<GeneralFormResult.Data> apply(GeneralFormResult generalFormResult) throws Exception {
                        return Observable.fromIterable(generalFormResult.getData());
                    }
                })
                .flatMap(new Function<GeneralFormResult.Data, ObservableSource<GeneralForm>>() {
                    @Override
                    public ObservableSource<GeneralForm> apply(GeneralFormResult.Data data) throws Exception {
                        //通用表单转换
                        GeneralForm generalForm = new GeneralForm(data.getReportcode(), data.getName());
                        if (data.getIsxls().equals("E")) {
                            generalForm.isTestPostForm = true;
                        }
                        for (GeneralFormResult.Data.DetailedFieldPad item : data.getDetailedFieldPad()) {

                            if (item.getItemname().equals("备注")) {
                                //过滤掉备注栏
                                continue;
                            }

                            int inputType = InputView.Companion.getTYPE_INVALID();
                            try {
                                inputType = Integer.parseInt(item.getControltype());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            int extraType = -1;
                            try {
                                extraType = Integer.parseInt(item.getIstop());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            String rowID;
                            switch (item.getModetype()) {
                                case "7":
                                    rowID = item.getItemcode() + "_" + item.getIstop();
                                    break;
                                default:
                                    rowID = item.getItemcode();
                            }

                            GeneralRow generalRow = new GeneralRow(
                                    rowID,
                                    item.getItemname(),
                                    item.getOrderno(),
                                    item.getItemcode(),
                                    item.getDefaultvlaue(),
                                    item.getLabelwidth(),
                                    inputType,
                                    //一般栏位isReadOnly = 1为必填栏位，初件栏位为1时是只读栏位
                                    item.getIsreadonly().equals("1"),
                                    item.getMorevalues(),
                                    item.getReportname(),
                                    extraType,
                                    item.getSetfield(),
                                    item.getGetfield()
                            );
                            generalForm.generalRows.add(generalRow);
                        }
                        return Observable.just(generalForm);
                    }
                });
    }

    @Override
    public Observable<MultiColumnForm> convertToMultiColFormList() {
        return Observable.concat(apiRequest.getMultiColFormTitles(FACTORY), apiRequest.getMultiColFormContents(FACTORY))
                .filter(new Predicate<Object>() {
                    @Override
                    public boolean test(Object o) throws Exception {
                        if (o instanceof MultiColFormTitleResult) {
                            tmpTitles = (MultiColFormTitleResult) o;
                            return false;
                        } else {
                            MultiColFormContentResult multiColFormResult = (MultiColFormContentResult) o;
                            for (MultiColFormContentResult.Data data : multiColFormResult.getData()) {
                                data.setTitles(tmpTitles);
                            }
                            return true;
                        }
                    }
                })
                .flatMap(new Function<Object, ObservableSource<MultiColFormContentResult.Data>>() {
                    @Override
                    public ObservableSource<MultiColFormContentResult.Data> apply(Object o) throws Exception {
                        MultiColFormContentResult formResult = null;
                        if (o instanceof MultiColFormContentResult) {
                            formResult = (MultiColFormContentResult) o;
                            return Observable.fromIterable(formResult.getData());
                        } else {
                            return null;
                        }
                    }
                })
                .flatMap(new Function<MultiColFormContentResult.Data, ObservableSource<MultiColumnForm>>() {
                    @Override
                    public ObservableSource<MultiColumnForm> apply(MultiColFormContentResult.Data data) throws Exception {
                        //对一个表单对象进行转换

                        //1、拿到表单的标题对象
                        MultiColFormTitleResult.Data title = null;
                        for (MultiColFormTitleResult.Data t : data.getTitles().getData()) {
                            if (t.getReportcode().equals(data.getCtid())) {
                                title = t;
                            }
                        }

                        if (title == null) {
                            title = data.getTitles().getData().get(0);
                        }

                        MultiColumnForm multiColumnForm = new MultiColumnForm(data.getReportcode(), data.getName());
                        if (data.getIsXls() != null && data.getIsXls().equals("E")) {
                            multiColumnForm.isTestForm = true;
                        }

                        Class<MultiColFormTitleResult.Data> titleCls = MultiColFormTitleResult.Data.class;
                        Class<MultiColFormContentResult.Data.DetailedFieldPad> rowCls = MultiColFormContentResult.Data.DetailedFieldPad.class;
                        for (MultiColFormContentResult.Data.DetailedFieldPad cell : data.getDetailedFieldPad()) {
                            //对每行进行转换
                            MultiColFormRow multiColFormRow = new MultiColFormRow();
                            for (int i = 0; i <= 6; i++) {
                                //最多七列，获取一行每列标题和内容
                                String titleStr = (String) titleCls.getMethod("getField" + (i + 1)).invoke(title);
                                String rowStr = (String) rowCls.getMethod("getField" + (i + 1)).invoke(cell);

                                if (i == 0) {
                                    //首列为标题行
                                    multiColFormRow.titleDesc = titleStr;
                                    multiColFormRow.title = rowStr;
                                } else if (rowStr.equals("[CONTROLTYPE]")) {
                                    //转换的最后一行为输入行
                                    int inputType = InputView.Companion.getTYPE_INVALID();
                                    try {
                                        inputType = Integer.parseInt(cell.getControltype());
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                    boolean isMustWrite = false;
                                    if (cell.getIsreadonly().equals("1")) {
                                        //一般栏位isReadOnly = 1为必填栏位，初件栏位为1时是只读栏位
                                        isMustWrite = true;
                                    }

                                    MultiColFormRow.InputRow inputRow = new MultiColFormRow.InputRow(
                                            cell.getOrderno(),
                                            titleStr,
                                            cell.getOrderno(),
                                            cell.getDefaultvlaue(),
                                            inputType,
                                            isMustWrite,
                                            cell.getMorevalues(),
                                            cell.getSetfield(),
                                            cell.getGetfield()
                                    );
                                    multiColFormRow.inputRow = inputRow;
                                    break;
                                } else {
                                    MultiColDisplayCell multiColDisplayCell = new MultiColDisplayCell(titleStr, rowStr);
                                    multiColFormRow.colDisplay.add(multiColDisplayCell);
                                }
                            }

                            if (multiColFormRow.inputRow != null && !multiColFormRow.title.equals("备注")) {
                                multiColumnForm.multiColFormRows.add(multiColFormRow);
                            }
                        }
                        return Observable.just(multiColumnForm);
                    }
                });
    }

    @Override
    public Observable<User> convertToUserList() {
        return apiRequest.getUserList(FACTORY)
                .flatMap(new Function<UserResult, ObservableSource<UserResult.Data>>() {
                    @Override
                    public ObservableSource<UserResult.Data> apply(UserResult userResult) throws Exception {
                        return Observable.fromIterable(userResult.getData());
                    }
                })
                .flatMap(new Function<UserResult.Data, ObservableSource<User>>() {
                    @Override
                    public ObservableSource<User> apply(UserResult.Data data) throws Exception {
                        User user = new User(data.getUserID(), data.getName(), "", data.getAuthList());
                        return Observable.just(user);
                    }
                });
    }

    @Override
    public Observable<CJRow> convertToCJRow() {
        return null;
    }

    public Observable<GeneralPostResult> boundNFCCard(String bindData) {
        return apiRequest.boundNFCCard(FACTORY, bindData);
    }

    @Override
    public Observable<Boolean> postFormData(String formData) {
        return apiRequest.postFormData(FACTORY, formData)
                .flatMap(new Function<GeneralPostResult, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(GeneralPostResult generalPostResult) throws Exception {
                        if (generalPostResult.getStatus().equalsIgnoreCase("Success")) {
                            return Observable.just(Boolean.TRUE);
                        } else {
                            return Observable.just(Boolean.FALSE);
                        }
                    }
                });
    }

    public Observable<WorkNo> loginAuth(String cardCode) {
        return apiRequest.getWorkNo(cardCode);
    }

    @Override
    public Observable<Formula> convertToFormula() {
        return apiRequest.getFormulas(FACTORY)
                .flatMap(new Function<FormulaResult, ObservableSource<FormulaResult.Data>>() {
                    @Override
                    public ObservableSource<FormulaResult.Data> apply(FormulaResult formulaResult) throws Exception {
                        return Observable.fromIterable(formulaResult.getData());
                    }
                })
                .flatMap(new Function<FormulaResult.Data, ObservableSource<Formula>>() {
                    @Override
                    public ObservableSource<Formula> apply(FormulaResult.Data data) throws Exception {
                        Formula formula = new Formula(
                                data.getInfocode(),
                                data.getInfoname(),
                                data.getInfotype(),
                                data.getInfotable(),
                                data.getInfokey(),
                                data.getInfovalue(),
                                data.getInfocon(),
                                data.getInfodatabase()
                        );
                        return Observable.just(formula);
                    }
                });
    }

    @Override
    public Observable<String> executeFormula(String formula, String parameters) {
        return apiRequest.executeFormula(FACTORY, formula, parameters)
                .flatMap(new Function<GeneralPostResult, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(GeneralPostResult generalPostResult) throws Exception {
                        if (generalPostResult.getStatus().equals("Success")) {
                            return Observable.just(generalPostResult.getMessage());
                        } else {
                            return Observable.just("计算失败");
                        }
                    }
                });
    }

    @Override
    public Observable<Boolean> bindLineLimitTime(String bindData) {
        return apiRequest.bindLineLimitTime(bindData)
                .flatMap(new Function<GeneralPostResult, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(GeneralPostResult generalPostResult) throws Exception {
                        if (generalPostResult.getStatus().equals("Success")) {
                            return Observable.just(true);
                        } else {
                            return Observable.just(false);
                        }
                    }
                });
    }

    @Override
    public Observable<FormPaperNo> postCJFormData(String formData) {
        return null;
    }

    @Override
    public Observable<CJCellValue> getCJCellValues(String reportcode, String partnum, String lineNo) {
        return null;
    }

    @Override
    public Observable<Boolean> postAuditJudgement(String workNo, String paperNo, String index, String auditdate, String audittime, String judgement, String reportcode, String audit) {
        return null;
    }

    @Override
    public Observable<AuditStatus> getAuditJudgement(String paperNo) {
        return null;
    }

    @Override
    public Observable<WorkNoInfo> getWorkNoInfo(String nfcTag) {
        return null;
    }

    @Override
    public Observable<UserMachineBound> convertUserMachineBound() {
        return null;
    }
}
