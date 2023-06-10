package com.example.mystory.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.mystory.data.Result
import com.example.mystory.data.api.respone.MessageResponse
import com.example.mystory.data.repository.StoryRepository
import com.example.mystory.ui.viewmodel.AddViewModel
import com.example.mystory.util.DataDummy
import com.example.mystory.util.MainDispatcherRule
import com.example.mystory.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository : StoryRepository
    private lateinit var addViewModel : AddViewModel
    private val dummyToken = "authentication_token"

    private val lat = 0.01F
    private val lon = 0.01F

    @Before
    fun setup(){
        addViewModel = AddViewModel(storyRepository)

    }


    @Test
    fun `when add story not null and return success`(){
        val dummyResponse = DataDummy.generateDummyMessageSuccess()
        val descriptionText = "Description Text"
        val description = descriptionText.toRequestBody("text/plain".toMediaType())
        val file = Mockito.mock(File::class.java)
        val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val imageMultiPart = MultipartBody.Part.createFormData(
            "name",
            "photo.jpg",
            requestImageFile
        )

        val expectedValue = MutableLiveData<Result<MessageResponse>>()
        expectedValue.value = Result.Success(dummyResponse)
        Mockito.`when`(
            storyRepository.uploadStory(
                dummyToken,
                imageMultiPart,
                description,
                lat,
                lon
            )
        ).thenReturn(expectedValue)

        val actualValue = addViewModel.addStory(
            dummyToken,
            imageMultiPart,
            description,
            lat,
            lon
        ).getOrAwaitValue()

        Mockito.verify(storyRepository).uploadStory(
            dummyToken,
            imageMultiPart,
            description,
            lat,
            lon
        )
        Assert.assertNotNull(actualValue)
        Assert.assertTrue(actualValue is Result.Success)
        Assert.assertEquals(dummyResponse.error, (actualValue as Result.Success).data.error)
    }

    @Test
    fun `when add story network error should return error`(){
        val descriptionText = "Description Text"
        val description = descriptionText.toRequestBody("text/plain".toMediaType())
        val file = Mockito.mock(File::class.java)
        val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val imageMultiPart = MultipartBody.Part.createFormData(
            "name",
            "photo.jpg",
            requestImageFile
        )

        val expectedValue = MutableLiveData<Result<MessageResponse>>()
        expectedValue.value = Result.Error("network error")

        Mockito.`when`(
            storyRepository.uploadStory(
                dummyToken,
                imageMultiPart,
                description,
                lat,
                lon
            )
        ).thenReturn(expectedValue)

        val actualResponse = storyRepository.uploadStory(
            dummyToken,
            imageMultiPart,
            description,
            lat,
            lon
        ).getOrAwaitValue()

        Mockito.verify(storyRepository).uploadStory(
            dummyToken,
            imageMultiPart,
            description,
            lat,
            lon
        )

        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Error)
    }
}