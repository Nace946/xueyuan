import request from '../utils/request'

export function list() {
  return request.get('/admin/banner/list')
}

export function save(data) {
  return request.post('/admin/banner', data)
}

export function update(id, data) {
  return request.put('/admin/banner/' + id, data)
}

export function remove(id) {
  return request.delete('/admin/banner/' + id)
}
