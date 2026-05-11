import './App.css'
import { useState } from 'react'

function App() {
  const [id, setId] = useState('')
  const [password, setPassword] = useState('')

  const handleLogin = async (e) => {
    e.preventDefault()

    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        usernameOrEmail: id,
        password,
      }),
    })

    if (!res.ok) {
      console.log('login failed', res.status)
      return
    }

    const data = await res.json()
    console.log('login success', data)
  }

  return (
    <>
      <input type="text" placeholder="ID or Email" onChange={(e) => setId(e.target.value)} />
      <input type="password" placeholder="PW" onChange={(e) => setPassword(e.target.value)} />
      <input type="submit" onClick={handleLogin} value="Login" />
    </>
  )
}

export default App
