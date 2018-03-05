package com.zdtco.datafetch;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.zdtco.datafetch.dao.AuditFormDao;
import com.zdtco.datafetch.dao.BoundMachineDao;
import com.zdtco.datafetch.dao.CJRowDao;
import com.zdtco.datafetch.dao.ContinuousLoginInfoDao;
import com.zdtco.datafetch.dao.FormCheckStatusDao;
import com.zdtco.datafetch.dao.FormPrintDao;
import com.zdtco.datafetch.dao.FormStubDao;
import com.zdtco.datafetch.dao.FormulaDao;
import com.zdtco.datafetch.dao.GeneralFormDao;
import com.zdtco.datafetch.dao.MachineAndFormDao;
import com.zdtco.datafetch.dao.MachineDao;
import com.zdtco.datafetch.dao.MultiColFormDao;
import com.zdtco.datafetch.dao.UserDao;
import com.zdtco.datafetch.dao.UserMachineBoundDao;
import com.zdtco.datafetch.data.AuditForm;
import com.zdtco.datafetch.data.BoundMachine;
import com.zdtco.datafetch.data.CJRow;
import com.zdtco.datafetch.data.ContinuousLoginInfo;
import com.zdtco.datafetch.data.FormCheckStatus;
import com.zdtco.datafetch.data.FormPrintData;
import com.zdtco.datafetch.data.FormStub;
import com.zdtco.datafetch.data.Formula;
import com.zdtco.datafetch.data.GeneralForm;
import com.zdtco.datafetch.data.Machine;
import com.zdtco.datafetch.data.MachineAndForm;
import com.zdtco.datafetch.data.MultiColumnForm;
import com.zdtco.datafetch.data.User;
import com.zdtco.datafetch.data.UserMachineBound;

/**
 * Created by G1494458 on 2017/12/8.
 */

@Database(entities = {Machine.class,
        GeneralForm.class,
        MultiColumnForm.class,
        User.class,
        FormPrintData.class,
        CJRow.class,
        FormStub.class,
        FormCheckStatus.class,
        Formula.class,
        AuditForm.class,
        ContinuousLoginInfo.class,
        MachineAndForm.class,
        UserMachineBound.class,
        BoundMachine.class
}, version = 12)
public abstract class FormDB extends RoomDatabase {
    abstract public MachineDao machineDao();

    abstract public GeneralFormDao generalFormDao();

    abstract public MultiColFormDao multiColFormDao();

    abstract public UserDao userDao();

    abstract public FormPrintDao formPrintDao();

    abstract public CJRowDao cjRowDao();

    abstract public FormStubDao formStubDao();

    abstract public FormCheckStatusDao formCheckStatusDao();

    abstract public FormulaDao formulaDao();

    abstract public AuditFormDao auditFormDao();

    abstract public ContinuousLoginInfoDao continuousLoginInfoDao();

    abstract public MachineAndFormDao machineAndFormDao();

    abstract public UserMachineBoundDao userMachineBoundDao();

    abstract public BoundMachineDao boundMachineDao();
}
