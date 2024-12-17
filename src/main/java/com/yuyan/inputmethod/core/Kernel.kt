package com.yuyan.inputmethod.core

import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.service.DecodingInfo
import com.yuyan.inputmethod.RimeEngine

object Kernel {

    /**
     * 初始化输入法
     */
    @Synchronized
    fun initImeSchema(schema: String) {
        RimeEngine.selectSchema(schema)
        nativeUpdateImeOption()
    }

    /**
     * 传入一个键码
     */
    fun inputKeyCode(keyCode: Int) {
        RimeEngine.onNormalKey(keyCode)
    }

    val isFinish: Boolean
        /**
         * 是否输入完毕，等待上屏。
         */
        get() = RimeEngine.isFinish()
    val candidates: Array<CandidateListItem>
        get() {
            return RimeEngine.showCandidates
        }
    val nextPageCandidates: Array<CandidateListItem>
        get() {
            return RimeEngine.getNextPageCandidates()
        }
    val prefixs: Array<String>
        /**
         * 拿到候选词拼音
         */
        get() {
            return RimeEngine.getPrefixs()
        }

    /**
     * 选择某个候选拼音
     */
    fun selectPrefix(index: Int) {
        RimeEngine.selectPinyin(index)
    }

    /**
     * 执行选择动作，选择了index指向的词语
     */
    fun getWordSelectedWord(index: Int) {
        if (DecodingInfo.isAssociate) {
            RimeEngine.selectAssociation(index)
        } else {
            RimeEngine.selectCandidate(index)
        }
    }

    val wordsShowPinyin: String
        /**
         * 最上端拼音行
         */
        get() = RimeEngine.showComposition
    val commitText: String
        /**
         * 得到即将上屏的候选词
         */
        get() {
            return RimeEngine.preCommitText
        }

    /**
     * 删除操作
     */
    fun deleteAction() {
        RimeEngine.onDeleteKey()
    }

    /**
     *  重置输入状态
     */
    fun reset() {
        RimeEngine.reset()
    }

    /**
     * 释放内存,重新初始化输入方案
     * 简体和繁体切换需要释放内存
     */
    fun resetIme() {
        RimeEngine.destroy()
        initImeSchema(AppPrefs.getInstance().internal.pinyinModeRime.getValue())
    }

    /**
     * 根据输入的字符查询候选词
     */
    fun getAssociateWord(words: String) {
        RimeEngine.predictAssociationWords(words)
    }

    /**
     * 刷新引擎配置
     */
    fun nativeUpdateImeOption() {
        val chineseFanTi = AppPrefs.getInstance().input.chineseFanTi.getValue()
        RimeEngine.setImeOption("traditionalization", chineseFanTi)
        val emojiInput = AppPrefs.getInstance().input.emojiInput.getValue()
        RimeEngine.setImeOption("emoji", emojiInput)
    }
}