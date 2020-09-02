package com.pullo.batch.processor

import com.luhuiguo.chinese.ChineseUtils
import com.pullo.batch.model.PoetryDTO
import com.pullo.batch.util.JsonConverter
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component


@Component
class PoetryProcessor : ItemProcessor<PoetryDTO, PoetryDTO> {
    @Throws(Exception::class)
    override fun process(poetryDTO: PoetryDTO): PoetryDTO {
        poetryDTO.text = ChineseUtils.toSimplified(poetryDTO.text)
        poetryDTO.title = ChineseUtils.toSimplified(poetryDTO.title)
        poetryDTO.author = ChineseUtils.toSimplified(poetryDTO.author)
        log.info(JsonConverter.serialize(poetryDTO))
        return poetryDTO
    }

    companion object {
        private val log = LoggerFactory.getLogger(PoetryProcessor::class.java)
    }
}
