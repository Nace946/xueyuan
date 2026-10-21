import request from '../utils/request'

export function page(params) {
  return request.get('/admin/house/page', { params })
}

export function get(id) {
  return request.get('/admin/house/' + id)
}

export function save(data) {
  return request.post('/admin/house', data)
}

export function update(id, data) {
  return request.put('/admin/house/' + id, data)
}

export function remove(id) {
  return request.delete('/admin/house/' + id)
}

export function updateStatus(id, status) {
  return request.post('/admin/house/' + id + '/status', null, { params: { status } })
}
