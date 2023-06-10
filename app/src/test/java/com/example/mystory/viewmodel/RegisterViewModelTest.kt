package com.example.mystory.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.mystory.data.Result
import com.example.mystory.data.api.respone.MessageResponse
import com.example.mystory.data.repository.StoryRepository
import com.example.mystory.ui.viewmodel.RegisterVewModel
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
class RegisterViewModelTest {
    @get:Rule
    val instanceExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var registerViewModel : RegisterVewModel
    private var dummyRegisterReponse = DataDummy.generateDummyMessageSuccess()

    private val dummyName = "name"
    private val dummyEmail = "email"
    private val dummyPassword = "password"

    @Before
    fun setup(){
        registerViewModel = RegisterVewModel(storyRepository)
    }

    @Test
    fun `when register should not null and success`(){
        val expectedValue = MutableLiveData<Result<MessageResponse>>()
        expectedValue.value = Result.Success(dummyRegisterReponse)
        `when`(storyRepository.registerUser(dummyName,dummyEmail,dummyPassword)).thenReturn(expectedValue)

        val actualValue = registerViewModel.registerUser(dummyName,dummyEmail,dummyPassword).getOrAwaitValue()

        Mockito.verify(storyRepository).registerUser(dummyName,dummyEmail,dummyPassword)
        Assert.assertNotNull(actualValue)
        Assert.assertTrue(actualValue is Result.Success)
    }

    @Test
    fun `when register should null and success`(){
        dummyRegisterReponse = DataDummy.generateDummyMessageError()
        val expectedValue = MutableLiveData<Result<MessageResponse>>()
        expectedValue.value = Result.Error("bad request")
        `when`(storyRepository.registerUser(dummyName,dummyEmail,dummyPassword)).thenReturn(expectedValue)

        val actualValue = registerViewModel.registerUser(dummyName,dummyEmail,dummyPassword).getOrAwaitValue()
        Mockito.verify(storyRepository).registerUser(dummyName,dummyEmail,dummyPassword)
        Assert.assertNotNull(actualValue)
        Assert.assertTrue(actualValue is Result.Error)
    }
}