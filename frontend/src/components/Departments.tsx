import { useEffect, useState } from 'react'
import {
    getDepartments,
    createDepartment,
    deleteDepartment
} from '../services/departmentService'
import type {Department} from '../types/Department'

export function Departments() {
    const [departments, setDepartments] = useState<Department[]>([])
    const [name, setName] = useState('')

    const load = async () => {
        try {
            const res = await getDepartments()
            // Make sure it's an array
            setDepartments(Array.isArray(res) ? res : [])
        } catch (e) {
            console.error('Failed to load departments', e)
            setDepartments([])
        }
    }


    useEffect(() => {
        load()
    }, [])

    const add = async () => {
        if (!name.trim()) return
        await createDepartment(name)
        setName('')
        load()
    }

    const remove = async (id: number) => {
        await deleteDepartment(id)
        load()
    }

    return (
        <div>
            <h2>Departments</h2>

            <input
                value={name}
                onChange={e => setName(e.target.value)}
                placeholder="Department name"
            />
            <button onClick={add}>Add</button>

            <ul>
                {departments.map(d => (
                    <li key={d.id}>
                        {d.name}
                        <button onClick={() => remove(d.id)}>Delete</button>
                    </li>
                ))}
            </ul>
        </div>
    )
}
