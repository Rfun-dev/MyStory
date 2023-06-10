package com.example.mystory.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.mystory.data.Result
import com.example.mystory.data.api.respone.LoginResponse
import com.example.mystory.data.repository.StoryRepository
import com.example.mystory.ui.viewmodel.LoginViewModel
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
class LoginViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var loginViewModel: LoginViewModel
    private var dummyLoginResponse = DataDummy.generateDummyLoginSuccess()
    private val email = "email"
    private val password = "password"

    @Before
    fun setup(){
        loginViewModel = LoginViewModel(storyRepository)
    }

    @Test
    fun `when Login should not null and return success`(){
        val expectedValue = MutableLiveData<Result<LoginResponse>>()
        expectedValue.value = Result.Success(dummyLoginResponse)
        `when`(storyRepository.loginUser(email,password)).thenReturn(expectedValue)

        val actualValue = loginViewModel.login(email, password).getOrAwaitValue()
        Mockito.verify(storyRepository).loginUser(email, password)
        Assert.assertNotNull(actualValue)
        Assert.assertTrue(actualValue is Result.Success)
    }

    @Test
    fun `when login should not null and return error`(){
        dummyLoginResponse = DataDummy.generateDummyLoginError()
        val expectedValue = MutableLiveData<Result<LoginResponse>>()
        expectedValue.value = Result.Error("invalid password")
        `when`(storyRepository.loginUser(email, password)).thenReturn(expectedValue)

        val actualValue = loginViewModel.login(email, password).getOrAwaitValue()
        Mockito.verify(storyRepository).loginUser(email, password)
        Assert.assertNotNull(actualValue)
        Assert.assertTrue(actualValue is Result.Error)
    }
}