package com.example.mystory.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.mystory.data.Result
import com.example.mystory.data.database.entity.StoryEntity
import com.example.mystory.data.model.StoryItem
import com.example.mystory.data.repository.StoryRepository
import com.example.mystory.ui.viewmodel.MapsViewModel
import com.example.mystory.util.DataDummy
import com.example.mystory.util.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapsViewModel {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mapsViewModel: MapsViewModel
    private val dummyResponse = DataDummy.generateDummyStories()

    private val dummyToken = "dummy_token"

    @Before
    fun setup(){
        mapsViewModel = MapsViewModel(storyRepository)
    }

    @Test
    fun `when getStoriesWithLocation should not null and return success`(){
        val expectedValue = MutableLiveData<Result<List<StoryItem>>>()
        expectedValue.value = Result.Success(convertToResponse(dummyResponse))
        `when`(storyRepository.getLocation(
            dummyToken,
        )).thenReturn(expectedValue)

        val actualValue = mapsViewModel.getLocation(dummyToken).getOrAwaitValue()
        Mockito.verify(storyRepository).getLocation(dummyToken)
        Assert.assertNotNull(actualValue)
        Assert.assertTrue(actualValue is Result.Success)
    }

    @Test
    fun `when getStoriesWithLocation should not null and return error`(){
        val expectedValue = MutableLiveData<Result<List<StoryItem>>>()
        expectedValue.value = Result.Error("error")
        `when`(storyRepository.getLocation(dummyToken)).thenReturn(expectedValue)

        val actualValue = mapsViewModel.getLocation(dummyToken).getOrAwaitValue()
        Mockito.verify(storyRepository).getLocation(dummyToken)
        Assert.assertNotNull(actualValue)
        Assert.assertTrue(actualValue is Result.Error)
    }

    private fun convertToResponse(list : List<StoryEntity>) : List<StoryItem> {
        return list.map {
            StoryItem(
                it.photoUrl.toString(),
                it.name.toString(),
                it.description.toString(),
                it.id,
                it.createdAt.toString(),
                it.lat?.toDouble() as Double,
                it.lon?.toDouble() as Double
            )
        }
    }
}