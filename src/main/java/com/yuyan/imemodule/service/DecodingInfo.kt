package com.yuyan.imemodule.service

import androidx.lifecycle.MutableLiveData
import com.yuyan.inputmethod.core.CandidateListItem
import com.yuyan.inputmethod.core.Kernel

/**
 * 词库解码操作对象
 */
object DecodingInfo {

    var activeCandidate = 0  //当前显示候选词位置
    var activeCandidateBar = 0  //当前显示候选词位置
    // 候选词列表
    val candidatesLiveData = MutableLiveData<List<CandidateListItem>>()

    // 是否是联想词
    var isAssociate = false
    // 是否重置
    private var isReset = false

    /**
     * 重置
     */
    fun reset() {
        isAssociate = false
        isReset = true
        activeCandidate = 0
        activeCandidateBar = 0
        candidatesLiveData.postValue(emptyList())
        Kernel.reset()
    }

    val isCandidatesListEmpty: Boolean
        // 候选词列表是否为空
        get() = candidatesLiveData.value.isNullOrEmpty()

    val candidateSize: Int
        // 候选词列表是否为空
        get() = if(isCandidatesListEmpty) 0 else candidatesLiveData.value!!.size


    val candidates: List<CandidateListItem>
        // 候选词列表是否为空
        get() = candidatesLiveData.value?:emptyList()

    // 增加拼写字符
    fun inputAction(keycode: Int) {
        isReset = false
        activeCandidate = 0
        activeCandidateBar = 0
        Kernel.inputKeyCode(keycode)
        isAssociate = false
    }

    /**
     * 选择拼音
     * @param position 选择的position
     */
    fun selectPrefix(position: Int) {
        activeCandidate = 0
        activeCandidateBar = 0
        Kernel.selectPrefix(position)
    }

    val prefixs: Array<String>  //获取拼音组合
        get() = Kernel.prefixs

    /**
     * 删除
     */
    fun deleteAction() {
        activeCandidate = 0
        activeCandidateBar = 0
        if (isEngineFinish || isAssociate) {
            reset()
        } else {
            Kernel.deleteAction()
        }
    }

    val isFinish: Boolean
        get() = isEngineFinish && isCandidatesListEmpty

    val isEngineFinish: Boolean
        get() = Kernel.isFinish

    val composingStrForDisplay: String   //获取显示的拼音字符串/
        get() = if(Kernel.wordsShowPinyin.isNotBlank())Kernel.wordsShowPinyin else if(candidateSize > 0) candidates[0].comment else ""

    val composingStrForCommit: String   // 获取输入的拼音字符串
        get() = Kernel.wordsShowPinyin.replace("'", "")

    val nextPageCandidates: Int   // 获取下一页的候选词
        get() {
            val cands = Kernel.nextPageCandidates
            if (cands.isNotEmpty()) {
                candidatesLiveData.postValue(candidatesLiveData.value?.plus(cands))
                return cands.size
            }
            return 0
        }

    /**
     * 如果candId〉0，就选择一个候选词，并且重新获取一个候选词列表，选择的候选词存放在mComposingStr中，通过mDecInfo.
     * getComposingStrActivePart()取出来。如果candId小于0 ，就对输入的拼音进行查询。
     */
    fun chooseDecodingCandidate(candId: Int): String {
        activeCandidate = 0
        activeCandidateBar = 0
        if (candId >= 0) Kernel.getWordSelectedWord(candId)
        return if(Kernel.candidates.isNotEmpty()){
            candidatesLiveData.postValue(Kernel.candidates.asList())
            Kernel.commitText
        } else if(candId in 0..<candidateSize){
            val choice = candidatesLiveData.value!![candId].text
            reset()
            choice
        } else ""
    }

    /**
     * 获得指定的候选词
     */
    fun getCandidate(candId: Int): CandidateListItem? {
        return candidatesLiveData.value?.getOrNull(candId)
    }

    // 更新候选词
    fun cacheCandidates(words: Array<CandidateListItem>) {
        activeCandidate = 0
        activeCandidateBar = 0
        isReset = false
        candidatesLiveData.postValue(words.asList())
    }

    /**
     * 根据输入的字符查询候选词
     */
    fun getAssociateWord(words: String) {
        Kernel.getAssociateWord(words)
    }
}