package com.zdtco.formui

import com.zdtco.datafetch.data.*

/**
 * Created by G1494458 on 2017/11/22.
 */
class Form(var formID: String,
           var machineID: String,
           var columnNum: Int,
           var timeLimit: Long,
           var isStub: Boolean,
           var generalForm: GeneralForm?,
           var multiColumnForm: MultiColumnForm?,
            var isTestForm: Boolean?
//           var generalRows: MutableList<InputRow>?,
//           var multiColColRows: MutableList<FormMultiColRow>?,
//           var mergeMultiRowItems: MutableList<FormMergeMultiRow>?
) {

    class Builder {
        private var formID: String = "default_name"
        private var machineID: String = ""
        private var formType: Int = 0
        private var timeLimit: Long = 0
        private var isStub: Boolean = false
        private var generalForm: GeneralForm? = null
        private var multiColumnForm: MultiColumnForm? = null

        fun setName(id: String) : Builder {
            this.formID = id
            return this
        }

        fun setMachineID(machineID: String) : Builder {
            this.machineID = machineID
            return this
        }

        fun setFormType(type: Int) : Builder {
            this.formType = type
            return this
        }

        fun setTimeLimit(timeLimit: Long) : Builder {
            this.timeLimit = timeLimit
            return this
        }

        fun isStub(isStub: Boolean) : Builder {
            this.isStub = isStub
            return this
        }

        fun generalForm(generalForm: GeneralForm?) : Builder {
            this.generalForm = generalForm
            return this
        }

        fun multiColumnForm(multiColumnForm: MultiColumnForm?) : Builder {
            this.multiColumnForm = multiColumnForm
            return this
        }
//
//        fun rowItems(rowItem: MutableList<InputRow>?) : Builder {
//            this.rowItem = rowItem
//            return this
//        }
//
//        fun mergeMultiRowItems(mergeMultiRowItems: MutableList<FormMergeMultiRow>?) : Builder {
//            this.mergeMultiRowItems = mergeMultiRowItems
//            return this
//        }
//
//        fun multiRowItems(multiColRowItems: MutableList<FormMultiColRow>?) : Builder {
//            this.multiColRowItems = multiColRowItems
//            return this
//        }

        fun build() : Form {
            if (generalForm != null) {
                return Form(generalForm?.id!!, machineID, formType, timeLimit, isStub, generalForm, null, generalForm?.isTestPostForm)
            } else if (multiColumnForm != null) {
                return Form(multiColumnForm?.formID!!, machineID, formType, timeLimit, isStub, null, multiColumnForm, multiColumnForm?.isTestForm)
            } else {
                throw IllegalStateException("must initialize form data")
            }
//            if (rowItem != null) {
//                return Form(name, formType, timeLimit, isStub, rowItem, null, null)
//            } else if (multiColRowItems != null) {
//                return Form(name, formType, timeLimit, isStub, null, multiColRowItems, null)
//            } else if (mergeMultiRowItems != null) {
//                return Form(name, formType, timeLimit, isStub, null, null, mergeMultiRowItems)
//            } else {
//                throw IllegalStateException("must initialize form row data")
//            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}