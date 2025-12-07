import { api } from '../api'
import type {Department} from '../types/Department'

export const getDepartments = async (): Promise<Department[]> => {
    const res = await api.get<Department[]>('/departments')
    return Array.isArray(res.data) ? res.data : []
}

export const createDepartment = async (name: string) => {
    await api.post('/departments', { name })
}

export const deleteDepartment = async (id: number) => {
    await api.delete(`/departments/${id}`)
}
