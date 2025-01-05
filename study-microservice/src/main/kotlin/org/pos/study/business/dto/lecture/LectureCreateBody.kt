package org.pos.study.business.dto.lecture

import com.fasterxml.jackson.annotation.JsonProperty

data class LectureRequestBody(
    @field:JsonProperty("lecture_id")val lectureId: String
)