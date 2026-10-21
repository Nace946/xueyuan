import request from '../utils/request'

export function page(params) {
  return request.get('/admin/job/page', { params })
}

export function get(id) {
  return request.get('/admin/job/' + id)
}

export function save(data) {
  return request.post('/admin/job', data)
}

export function update(id, data) {
  return request.put('/admin/job/' + id, data)
}

export function remove(id) {
  return request.delete('/admin/job/' + id)
}

export function updateStatus(id, status) {
  return request.post('/admin/job/' + id + '/status', null, { params: { status } })
}
